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
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

/**
 * View for using Inertia with Spring MVC.
 *
 * @author Michael Yan
 * @since 0.5
 */
public class InertiaView extends AbstractView {

    public static final String INERTIA_VIEW_BEAN_NAME = "inertiaView";
    private static final String INERTIA_ROOT_TEMPLATE_NAME_DEFAULT = "inertia";

    private final ContentNegotiatingViewResolver viewResolver;
    private final ObjectMapper objectMapper;
    private final InertiaVersionProvider inertiaVersionProvider;
    private String defaultRootTemplateName = INERTIA_ROOT_TEMPLATE_NAME_DEFAULT;

    public InertiaView(ApplicationContext applicationContext, ContentNegotiatingViewResolver viewResolver, InertiaVersionProvider inertiaVersionProvider) {
        setApplicationContext(applicationContext);
        this.viewResolver = viewResolver;
        this.inertiaVersionProvider = inertiaVersionProvider;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public String getDefaultRootTemplateName() {
        return defaultRootTemplateName;
    }

    public void setDefaultRootTemplateName(String defaultRootTemplateName) {
        this.defaultRootTemplateName = defaultRootTemplateName;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void renderMergedOutputModel(Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        InertiaPage inertiaPage = InertiaPage
                .of((String) model.remove(InertiaPage.COMPONENT))
                .props((Map<String, Object>) model.remove(InertiaPage.PROPS))
                .url(request.getRequestURI())
                .version(this.inertiaVersionProvider.getVersion());

        String jsonPage = this.objectMapper.writeValueAsString(inertiaPage);

        if (isInertiaRequest(request)) {
            renderJson(model, jsonPage, request, response);
        }
        else {
            model.put("page", inertiaPage);
            model.put("pageData", jsonPage);
            renderHtml(model, jsonPage, request, response);
        }
    }

    protected void renderJson(Map<String, Object> model, String jsonPage,
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
     * Check request whether is Inertia or not
     *
     * @param request The HttpServletRequest
     * @return true If the request is Inertia else false
     */
    private boolean isInertiaRequest(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getHeader(InertiaHeaders.INERTIA));
    }

}
