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

import groovy.json.JsonSlurper
import org.springframework.http.HttpStatus

import static HttpServletRequestExtension.isInertia

/**
 * Provide inertiaManifest object to model,
 * and check asset client version and server version.
 *
 * @author Michael Yan
 * @since 0.5
 */
class InertiaInterceptor {

    private InertiaVersionProvider versionProvider
    private volatile Object manifestObject

    InertiaInterceptor() {
        match(controller: '*')
    }

    boolean after() {
        String assetVersion = getVersionProvider()?.version

        if (webRequest.renderView) {
            def manifest = getInertiaManifest()
            model.put(InertiaSettings.INERTIA_MANIFEST_OBJECT, manifest)
        }

        if (isInertiaRequest()) {
            if (isGetRequest() && isVersionStale()) {
                header(InertiaHeaders.X_INERTIA_LOCATION, webRequest.currentRequest.forwardURI)
                header(InertiaHeaders.X_INERTIA_VERSION, assetVersion)
                render(status: HttpStatus.CONFLICT.value())
                return false
            }
        }

        true
    }

    Object getInertiaManifest() {
        if (manifestObject == null) {
            String manifestLocation = grailsApplication.config.getRequiredProperty(InertiaSettings.INERTIA_MANIFEST_LOCATION)
            synchronized(this) {
                if (manifestObject == null) {
                    def manifestResource = grailsApplication.mainContext.getResource(manifestLocation)
                    manifestObject = new JsonSlurper().parse(manifestResource.inputStream)
                }
            }
        }
        manifestObject
    }

    InertiaVersionProvider getVersionProvider() {
        if (this.versionProvider == null) {
            this.versionProvider = grailsApplication.mainContext.getBean(InertiaVersionProvider.BEAN_NAME)
        }
        this.versionProvider
    }

    private boolean isVersionStale() {
        String clientVersion = request.getHeader(InertiaHeaders.X_INERTIA_VERSION)
        String serverVersion = getVersionProvider()?.version
        clientVersion != serverVersion
    }

    private boolean isGetRequest() {
        request.method?.toUpperCase() == 'GET'
    }

    private boolean isRedirectRequest() {
        response.status in [301, 302]
    }

    private boolean isInertiaRequest() {
        isInertia(request)
    }

}
