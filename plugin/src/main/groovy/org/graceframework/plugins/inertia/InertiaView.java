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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

/**
 * View for using Inertia with Spring MVC.
 *
 * @author Michael Yan
 * @since 0.5
 */
public class InertiaView extends AbstractView {

    public static final String INERTIA_VIEW = "inertiaView";

    private static final String INERTIA_TEMPLATE_LOCATION = "classpath:/templates/inertia/inertia.html";
    private static final String INERTIA_TEMPLATE_PLACEHOLDER = "@inertia";
    private static final String INERTIA_ROOT_TAG = "<div id='app' data-page='%s'></div>";

    private final String template;
    private final ObjectMapper objectMapper;
    private final InertiaVersionProvider inertiaVersionProvider;

    public InertiaView(ApplicationContext applicationContext, InertiaVersionProvider inertiaVersionProvider) {
        setApplicationContext(applicationContext);
        this.inertiaVersionProvider = inertiaVersionProvider;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.template = createTemplate(INERTIA_TEMPLATE_LOCATION);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void renderMergedOutputModel(Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        InertiaPage inertiaPage = InertiaPage
                .of((String) model.get(InertiaPage.COMPONENT))
                .props((Map<String, Object>) model.get(InertiaPage.PROPS))
                .url(request.getRequestURI())
                .version(this.inertiaVersionProvider.getVersion());

        String jsonPage = this.objectMapper.writeValueAsString(inertiaPage);

        if (isInertiaRequest(request)) {
            renderJson(jsonPage, request, response);
        }
        else {
            renderHtml(jsonPage, request, response);
        }
    }

    private void renderJson(String jsonPage, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.VARY, InertiaHeaders.INERTIA);
        response.setHeader(InertiaHeaders.INERTIA, "true");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(jsonPage);
    }

    private void renderHtml(String jsonPage, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String inertiaTag = String.format(INERTIA_ROOT_TAG, jsonPage);
        String html = this.template.replace(INERTIA_TEMPLATE_PLACEHOLDER, inertiaTag);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setContentLength(html.length());
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(html);
    }

    private String createTemplate(String location) {
        Resource resource = getWebApplicationContext().getResource(location);
        if (!resource.isReadable()) {
            throw new IllegalArgumentException("Missing template file " + location);
        }

        try {
            Reader reader = new InputStreamReader(resource.getInputStream());
            return FileCopyUtils.copyToString(reader);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not read the template file" + location);
        }
    }

    /**
     * Check request whether is Inertia or not
     *
     * @param request The HttpServletRequest
     * @return true If the request is Inertia else false
     */
    public static boolean isInertiaRequest(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getHeader(InertiaHeaders.INERTIA));
    }

}
