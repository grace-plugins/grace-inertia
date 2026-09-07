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
import groovy.transform.Generated
import groovy.transform.SelfType
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.ModelAndView

import grails.artefact.Enhances
import grails.artefact.controller.support.ResponseRenderer
import grails.converters.JSON
import grails.plugins.GrailsPlugin
import grails.plugins.GrailsPluginManager
import grails.util.GrailsWebUtil
import grails.web.http.HttpHeaders
import grails.web.mime.MimeType

import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.sitemesh.GrailsLayoutDecoratorMapper
import org.grails.web.util.GrailsApplicationAttributes

import static InertiaPage.INERTIA
import static InertiaPage.COMPONENT
import static InertiaPage.PROPS
import static InertiaPage.VIEW_DATA
import static org.grails.plugins.web.controllers.metaclass.RenderDynamicMethod.ARGUMENT_CONTEXTPATH
import static org.grails.plugins.web.controllers.metaclass.RenderDynamicMethod.ARGUMENT_LAYOUT
import static org.grails.plugins.web.controllers.metaclass.RenderDynamicMethod.ARGUMENT_MODEL
import static org.grails.plugins.web.controllers.metaclass.RenderDynamicMethod.ARGUMENT_PLUGIN
import static org.grails.plugins.web.controllers.metaclass.RenderDynamicMethod.ARGUMENT_VIEW

/**
 * Inertia trait
 *
 * @author Michael Yan
 * @since 0.5
 */
@CompileStatic
@SelfType(ResponseRenderer)
@Enhances(['Controller', 'Interceptor'])
trait InertiaTrait {

    private GrailsPluginManager pluginManager
    private InertiaVersionProvider versionProvider

    @Generated
    void render(Map argMap) {
        if (argMap.containsKey(INERTIA)) {
            String component = null
            Map props = null
            def inertia = argMap[INERTIA]
            if (inertia instanceof String) {
                component = (String) inertia
            }
            else if (inertia instanceof Map) {
                props = (Map) inertia
            }
            component = component ?: getDefaultComponent()
            props = props ?: (Map) argMap[PROPS]
            render(argMap, component, props)
        }
        else if (argMap.containsKey(COMPONENT)) {
            String component = argMap[COMPONENT] ?: getDefaultComponent()
            Map props = (Map) argMap[PROPS]
            render(argMap, component, props)
        }
        else if (argMap.containsKey(PROPS)) {
            String component = getDefaultComponent()
            Map props = (Map) argMap[PROPS]
            render(argMap, component, props)
        }
        else {
            ((ResponseRenderer) this).render(argMap)
        }
    }

    @Generated
    void render(String component, Map props) {
        Map argMap = [:]
        argMap.put(PROPS, props)
        render argMap, component
    }

    @Generated
    void render(Map argMap, String component, Map props) {
        argMap = argMap ?: [:]
        argMap.put(PROPS, props)
        render argMap, component
    }

    @Generated
    void render(Map argMap, String component) {
        GrailsWebRequest webRequest = (GrailsWebRequest) RequestContextHolder.currentRequestAttributes()
        HttpServletRequest request = webRequest.request
        HttpServletResponse response = webRequest.currentResponse
        String explicitSiteMeshLayout = argMap[ARGUMENT_LAYOUT]?.toString() ?: null
        def applicationAttributes = webRequest.attributes

        String version = getVersionProvider(webRequest).version
        InertiaPage inertiaPage = InertiaPage.of(component, (Map) argMap[PROPS] ?: argMap)
        inertiaPage.setVersion(version)
        inertiaPage.setUrl(HttpServletRequestExtension.getUrl(request))

        boolean isInertiaRequest = isInertiaRequest(request)
        if (isInertiaRequest) {
            response.setContentType GrailsWebUtil.getContentType(MimeType.JSON.name, GrailsWebUtil.DEFAULT_ENCODING)
            response.setHeader(HttpHeaders.VARY, InertiaHeaders.INERTIA)
            response.setHeader(InertiaHeaders.INERTIA, 'true')
            JSON json = new JSON(inertiaPage)
            json.setExcludes(['viewData'])
            json.render response
            webRequest.renderView = false
        }
        else {
            response.setContentType GrailsWebUtil.getContentType(MimeType.HTML.name, GrailsWebUtil.DEFAULT_ENCODING)
            String viewName = argMap[ARGUMENT_VIEW] ?: getDefaultRootViewName()
            String viewUri = applicationAttributes.getNoSuffixViewURI((GroovyObject) this, viewName)
            String contextPath = getContextPath(webRequest, argMap)
            if (contextPath) {
                viewUri = contextPath + viewUri
            }
            Object modelObject = argMap[VIEW_DATA] ?: argMap[ARGUMENT_MODEL]
            Map model
            if (modelObject instanceof Map) {
                model = (Map) modelObject
            }
            else {
                model = [:]
            }
            JSON json = new JSON(inertiaPage)
            json.setExcludes(['viewData'])
            String page = json.toString()
            request.setAttribute(InertiaSettings.INERTIA_PAGE_ATTRIBUTE, page)
            request.setAttribute(GrailsApplicationAttributes.CONTROLLER, null)
            request.setAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE, null)
            ((GroovyObject) this).setProperty 'modelAndView', new ModelAndView(viewUri, model)
            applySiteMeshLayout(webRequest.currentRequest, true, explicitSiteMeshLayout)
        }
    }

    String getDefaultRootViewName() {
        def config = getGrailsApplication().config
        if (config) {
            return config.getProperty(InertiaSettings.INERTIA_INITIAL_PAGE_ROOT_TEMPLATE_NAME, InertiaSettings.INERTIA_INITIAL_PAGE_ROOT_TEMPLATE_NAME_DEFAULT)
        }
        return InertiaSettings.INERTIA_INITIAL_PAGE_ROOT_TEMPLATE_NAME_DEFAULT
    }

    private void applySiteMeshLayout(HttpServletRequest request, boolean renderView, String explicitSiteMeshLayout) {
        if (explicitSiteMeshLayout == null && request.getAttribute(GrailsLayoutDecoratorMapper.LAYOUT_ATTRIBUTE) != null) {
            // layout has been set already
            return
        }
        String siteMeshLayout = explicitSiteMeshLayout != null ? explicitSiteMeshLayout :
                (renderView ? null : GrailsLayoutDecoratorMapper.NONE_LAYOUT)
        if (siteMeshLayout != null) {
            request.setAttribute(GrailsLayoutDecoratorMapper.LAYOUT_ATTRIBUTE, siteMeshLayout)
        }
    }

    private String getContextPath(GrailsWebRequest webRequest, Map argMap) {
        def cp = argMap.get(ARGUMENT_CONTEXTPATH)
        String contextPath = (cp != null ? cp.toString() : '')

        Object pluginName = argMap.get(ARGUMENT_PLUGIN)
        if (pluginName != null) {
            GrailsPlugin plugin = getPluginManager(webRequest).getGrailsPlugin(pluginName.toString())
            if (plugin != null && !plugin.isBasePlugin()) {
                contextPath = plugin.getPluginPath()
            }
        }
        contextPath
    }

    private GrailsPluginManager getPluginManager(GrailsWebRequest webRequest) {
        if (pluginManager == null) {
            pluginManager = webRequest.getApplicationContext().getBean(GrailsPluginManager)
        }
        pluginManager
    }

    private InertiaVersionProvider getVersionProvider(GrailsWebRequest webRequest) {
        if (versionProvider == null) {
            versionProvider = webRequest.getApplicationContext().getBean(InertiaVersionProvider)
        }
        versionProvider
    }

    private String getDefaultComponent() {
        "${controllerName}/${actionName}".toString()
    }

    private boolean isInertiaRequest(HttpServletRequest request) {
        return HttpServletRequestExtension.isInertia(request)
    }

}