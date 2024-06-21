/*
 * Copyright 2024 the original author or authors.
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

import grails.artefact.Enhances
import grails.converters.JSON
import grails.web.api.WebAttributes

/**
 * A trait that adds behavior to allow rendering Inertia page object to the response
 *
 * @author Michael Yan
 * @since 0.2
 */
@CompileStatic
@Enhances(['Controller', 'Interceptor'])
trait InertiaPageRenderer extends WebAttributes {

    @Generated
    void render(String component, Map props) {
        webRequest.renderView = false

        if (isInertia()) {
            renderJson(component, props)
        }
        else if (isSsrEnabled()) {
            renderSsr(component, props)
        }
        else {
            renderHtml(component, props)
        }
    }

    private void renderJson(String component, Map props) {
        HttpServletRequest request = webRequest.currentRequest
        HttpServletResponse response = webRequest.currentResponse

        String method = request.method
        String version = grailsApplication.getConfig().getProperty('inertia.version', '1.0')
        String assetVersion = request.getHeader(InertiaRequest.X_INERTIA_VERSION)
        if (method.toUpperCase() == 'GET' && version != assetVersion) {
            response.setHeader(InertiaRequest.X_INERTIA_LOCATION, request.requestURL.toString())
            response.setStatus(409)
            return
        }

        response.setHeader('Vary', 'Accept')
        response.setHeader(InertiaRequest.X_INERTIA, 'true')

        Map<String, Object> inertiaPage = new LinkedHashMap<>()
        inertiaPage.component = component
        inertiaPage.props = props
        inertiaPage.url = request.requestURL
        inertiaPage.version = version

        JSON json = new JSON(inertiaPage)
        json.render response
    }

    private void renderSsr(String component, Map props) {
        String ssrUrl = grailsApplication.getConfig().getProperty('inertia.ssr.url', 'http://localhost:13714')
    }

    private void renderHtml(String component, Map props) {

    }

    private boolean isInertia() {
        return Boolean.parseBoolean(webRequest.currentRequest.getHeader(InertiaRequest.X_INERTIA))
    }

    private boolean isSsrEnabled() {
        String enabled = grailsApplication.getConfig().getProperty('inertia.ssr.enabled', 'false')
        return Boolean.parseBoolean(enabled)
    }

}
