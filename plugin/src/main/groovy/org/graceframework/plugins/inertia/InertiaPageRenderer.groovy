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

/**
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
    void render(InertiaPage object, RenderContext context) {
        ServletRenderContext renderContext = (ServletRenderContext) context
        HttpServletRequest request = renderContext.getWebRequest().request
        HttpServletResponse response = renderContext.getWebRequest().response

        Map<String, Object> inertiaPage = new LinkedHashMap<>()
        inertiaPage.component = object.component ?: "${context.controllerName}/${context.actionName}"
        inertiaPage.props = object.getProps()
        inertiaPage.url = HttpServletRequestExtension.getUrl(request)
        inertiaPage.version = this.inertiaVersionProvider.version
        JSON json = new JSON(inertiaPage)

        if (isInertiaRequest(request)) {
            response.setHeader(HttpHeaders.VARY, InertiaRequest.X_INERTIA)
            response.setHeader(InertiaRequest.X_INERTIA, 'true')
            context.setContentType(GrailsWebUtil.getContentType(MimeType.JSON.name, GrailsWebUtil.DEFAULT_ENCODING))
            context.setStatus(HttpStatus.OK)
            json.render(context.writer)
        }
        else {
            context.setContentType(MimeType.HTML.name)
            String viewName = this.config.getProperty(InertiaSettings.INERTIA_TEMPLATE, String, InertiaSettings.INERTIA_TEMPLATE_DEFAULT)
            context.viewName = viewName
            context.setModel(object.getViewData())

            String page = json.toString()
            request.setAttribute(InertiaSettings.INERTIA_PAGE_ATTRIBUTE, page)
            request.setAttribute(GrailsApplicationAttributes.CONTROLLER, null)
            request.setAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE, null)
            request.setAttribute GrailsLayoutDecoratorMapper.LAYOUT_ATTRIBUTE, GrailsLayoutDecoratorMapper.NONE_LAYOUT
            response.setContentType GrailsWebUtil.getContentType(MimeType.HTML.name, GrailsWebUtil.DEFAULT_ENCODING)

            response.status = 200
            context.setModel(inertiaPage)
        }
    }

    private boolean isInertiaRequest(HttpServletRequest request) {
        return HttpServletRequestExtension.isInertia(request)
    }

}
