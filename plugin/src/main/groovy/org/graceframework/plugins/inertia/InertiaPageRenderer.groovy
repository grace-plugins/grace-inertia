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
package org.graceframework.plugins.inertia

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import groovy.transform.CompileStatic
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils

import grails.config.Config
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.rest.render.AbstractRenderer
import grails.rest.render.RenderContext
import grails.util.GrailsWebUtil
import grails.web.http.HttpHeaders
import grails.web.mime.MimeType

import org.grails.plugins.web.rest.render.ServletRenderContext
import org.grails.web.sitemesh.GrailsLayoutDecoratorMapper
import org.grails.web.util.GrailsApplicationAttributes

import static org.graceframework.plugins.inertia.InertiaPage.VIEW_DATA

/**
 * Render {@link InertiaPage} instance in JSON-encoded page object for the initial page,
 * Inertia uses this information to boot the client-side framework and display the initial page component.
 *
 * @author Michael Yan
 * @since 0.5
 */
@CompileStatic
class InertiaPageRenderer extends AbstractRenderer<InertiaPage> {

    public static final MimeType[] INERTIA_MIME_TYPES = [MimeType.ALL, MimeType.HTML, MimeType.JSON] as MimeType[]

    private GrailsApplication grailsApplication
    private Config config
    private InertiaVersionProvider inertiaVersionProvider

    InertiaPageRenderer(Class<InertiaPage> targetType, GrailsApplication grailsApplication,
            InertiaVersionProvider inertiaVersionProvider) {
        super(targetType, INERTIA_MIME_TYPES)
        this.grailsApplication = grailsApplication
        this.config = grailsApplication.config
        this.inertiaVersionProvider = inertiaVersionProvider
    }

    @Override
    void render(InertiaPage inertiaPage, RenderContext context) {
        ServletRenderContext renderContext = (ServletRenderContext) context
        HttpServletRequest request = renderContext.getWebRequest().request
        HttpServletResponse response = renderContext.getWebRequest().response

        if (!StringUtils.hasLength(inertiaPage.getComponent())) {
            inertiaPage.setComponent(getDefaultComponent(context))
        }
        if (!StringUtils.hasLength(inertiaPage.getUrl())) {
            inertiaPage.setUrl(HttpServletRequestExtension.getUrl(request))
        }
        if (!StringUtils.hasLength(inertiaPage.getVersion())) {
            inertiaPage.setVersion(this.inertiaVersionProvider.version)
        }

        if (isInertiaRequest(request)) {
            response.setHeader(HttpHeaders.VARY, InertiaHeaders.INERTIA)
            response.setHeader(InertiaHeaders.INERTIA, 'true')
            context.setContentType(GrailsWebUtil.getContentType(MimeType.JSON.name, GrailsWebUtil.DEFAULT_ENCODING))
            context.setStatus(HttpStatus.OK)
            JSON json = new JSON(inertiaPage)
            json.setExcludes([VIEW_DATA])
            json.render(context.writer)
        }
        else {
            Map<String, Object> model = new LinkedHashMap<>()
            model.put(InertiaSettings.INERTIA_PAGE_ATTRIBUTE, inertiaPage)
            model.putAll(inertiaPage.viewData)
            context.setContentType(MimeType.HTML.name)
            context.setViewName(getRootViewName())
            context.setModel(model)

            request.setAttribute(GrailsApplicationAttributes.CONTROLLER, null)
            request.setAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE, null)
            request.setAttribute GrailsLayoutDecoratorMapper.LAYOUT_ATTRIBUTE, GrailsLayoutDecoratorMapper.NONE_LAYOUT
            response.setContentType GrailsWebUtil.getContentType(MimeType.HTML.name, GrailsWebUtil.DEFAULT_ENCODING)
            response.status = 200
        }
    }

    String getRootViewName() {
        return this.config.getProperty(InertiaSettings.INERTIA_INITIAL_PAGE_ROOT_TEMPLATE_NAME,
                String, InertiaSettings.INERTIA_INITIAL_PAGE_ROOT_TEMPLATE_NAME_DEFAULT)
    }

    String getDefaultComponent(RenderContext context) {
        "${context.controllerName}/${context.actionName}".toString()
    }

    private boolean isInertiaRequest(HttpServletRequest request) {
        return HttpServletRequestExtension.isInertia(request)
    }

}
