/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.graceframework.plugins.inertia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

/**
 * HandlerMethodReturnValueHandler to handle return value of {@link InertiaResponse}.
 *
 * @author Michael Yan
 * @since 0.5
 */
public class InertiaResponseReturnValueHandler implements HandlerMethodReturnValueHandler {

    private static final String INERTIA_ROOT_TEMPLATE_NAME_DEFAULT = "inertia";
    private final InertiaVersionProvider inertiaVersionProvider;
    private final ObjectMapper objectMapper;
    private final ContentNegotiatingViewResolver viewResolver;
    private String defaultRootTemplateName = INERTIA_ROOT_TEMPLATE_NAME_DEFAULT;

    public InertiaResponseReturnValueHandler(ContentNegotiatingViewResolver viewResolver, InertiaVersionProvider inertiaVersionProvider) {
        this.viewResolver = viewResolver;
        this.inertiaVersionProvider = inertiaVersionProvider;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public String getDefaultRootTemplateName() {
        return this.defaultRootTemplateName;
    }

    public void setDefaultRootTemplateName(String defaultRootTemplateName) {
        this.defaultRootTemplateName = defaultRootTemplateName;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return InertiaResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);

        Assert.isInstanceOf(InertiaResponse.class, returnValue);
        String requestUrl = HttpServletRequestExtension.getUrl(inputMessage.getServletRequest());

        InertiaResponse inertiaResponse = (InertiaResponse) returnValue;
        HttpHeaders outputHeaders = outputMessage.getHeaders();
        HttpHeaders entityHeaders = inertiaResponse.getHeaders();

        if (!entityHeaders.isEmpty()) {
            entityHeaders.forEach((key, value) -> {
                if (HttpHeaders.VARY.equals(key) && outputHeaders.containsKey(HttpHeaders.VARY)) {
                    List<String> values = getVaryRequestHeadersToAdd(outputHeaders, entityHeaders);
                    if (!values.isEmpty()) {
                        outputHeaders.setVary(values);
                    }
                }
                else {
                    outputHeaders.put(key, value);
                }
            });
        }

        int returnStatus = inertiaResponse.getStatusCodeValue();
        outputMessage.getServletResponse().setStatus(returnStatus);

        if (returnStatus == 200) {
            HttpMethod method = inputMessage.getMethod();
            if ((HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method))
                    && isResourceNotModified(inputMessage, outputMessage)) {
                outputMessage.flush();
                return;
            }
        }
        else if (returnStatus / 100 == 3) {
            String location = outputHeaders.getFirst("location");
            if (location != null) {
                saveFlashAttributes(mavContainer, webRequest, location);
            }
        }

        InertiaPage inertiaPage = InertiaPage
                .of(inertiaResponse.getComponent())
                .props(inertiaResponse.getProps())
                .url(requestUrl)
                .version(this.inertiaVersionProvider.getVersion());

        String jsonPage = this.objectMapper.writeValueAsString(inertiaPage);

        if (isInertiaRequest(inputMessage.getServletRequest())) {
            mavContainer.setRequestHandled(true);
            renderJson(jsonPage, inputMessage.getServletRequest(), outputMessage.getServletResponse());
        }
        else {
            Map<String, Object> model = new LinkedHashMap<>();
            model.put("page", inertiaPage);
            model.put("pageData", jsonPage);
            model.putAll(inertiaResponse.getViewData());
            renderHtml(model, jsonPage, inputMessage.getServletRequest(), outputMessage.getServletResponse());
        }

        // Ensure headers are flushed even if no body was written.
        outputMessage.flush();
    }

    protected void renderJson(String jsonPage,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.VARY, InertiaHeaders.INERTIA);
        response.setHeader(InertiaHeaders.INERTIA, "true");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(jsonPage);
    }

    protected void renderHtml(Map<String, Object> model, String jsonPage,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(InertiaSettings.INERTIA_PAGE_ATTRIBUTE, jsonPage);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setStatus(HttpStatus.OK.value());
        View inertiaView = this.viewResolver.resolveViewName(getDefaultRootTemplateName(), Locale.getDefault());
        inertiaView.render(model, request, response);
    }

    /**
     * Create a new {@link HttpInputMessage} from the given {@link NativeWebRequest}.
     * @param webRequest the web request to create an input message from
     * @return the input message
     */
    protected ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "No HttpServletRequest");
        return new ServletServerHttpRequest(servletRequest);
    }

    /**
     * Creates a new {@link HttpOutputMessage} from the given {@link NativeWebRequest}.
     * @param webRequest the web request to create an output message from
     * @return the output message
     */
    protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        return new ServletServerHttpResponse(response);
    }

    private List<String> getVaryRequestHeadersToAdd(HttpHeaders responseHeaders, HttpHeaders entityHeaders) {
        List<String> entityHeadersVary = entityHeaders.getVary();
        List<String> vary = responseHeaders.get(HttpHeaders.VARY);
        if (vary != null) {
            List<String> result = new ArrayList<>(entityHeadersVary);
            for (String header : vary) {
                for (String existing : StringUtils.tokenizeToStringArray(header, ",")) {
                    if ("*".equals(existing)) {
                        return Collections.emptyList();
                    }
                    for (String value : entityHeadersVary) {
                        if (value.equalsIgnoreCase(existing)) {
                            result.remove(value);
                        }
                    }
                }
            }
            return result;
        }
        return entityHeadersVary;
    }

    private boolean isResourceNotModified(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        ServletWebRequest servletWebRequest =
                new ServletWebRequest(request.getServletRequest(), response.getServletResponse());
        HttpHeaders responseHeaders = response.getHeaders();
        String etag = responseHeaders.getETag();
        long lastModifiedTimestamp = responseHeaders.getLastModified();
        if (request.getMethod() == HttpMethod.GET || request.getMethod() == HttpMethod.HEAD) {
            responseHeaders.remove(HttpHeaders.ETAG);
            responseHeaders.remove(HttpHeaders.LAST_MODIFIED);
        }

        return servletWebRequest.checkNotModified(etag, lastModifiedTimestamp);
    }

    private void saveFlashAttributes(ModelAndViewContainer mav, NativeWebRequest request, String location) {
        mav.setRedirectModelScenario(true);
        ModelMap model = mav.getModel();
        if (model instanceof RedirectAttributes) {
            Map<String, ?> flashAttributes = ((RedirectAttributes) model).getFlashAttributes();
            if (!CollectionUtils.isEmpty(flashAttributes)) {
                HttpServletRequest req = request.getNativeRequest(HttpServletRequest.class);
                HttpServletResponse res = request.getNativeResponse(HttpServletResponse.class);
                if (req != null) {
                    RequestContextUtils.getOutputFlashMap(req).putAll(flashAttributes);
                    if (res != null) {
                        RequestContextUtils.saveOutputFlashMap(location, req, res);
                    }
                }
            }
        }
    }

    /**
     * Check request whether is Inertia or not
     *
     * @param request The HttpServletRequest
     * @return true If the request is Inertia else false
     */
    private boolean isInertiaRequest(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getHeader(InertiaHeaders.INERTIA));
    }

}
