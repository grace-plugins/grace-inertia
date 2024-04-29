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

import groovy.transform.CompileStatic

/**
 * Inertia Protocol {link https://inertiajs.com/the-protocol}
 *
 * @author Michael Yan
 * @since 0.1
 */
@CompileStatic
class InertiaRequest {

    public static final String X_INERTIA = 'X-Inertia'
    public static final String X_INERTIA_VERSION = 'X-Inertia-Version'
    public static final String X_INERTIA_LOCATION = 'X-Inertia-Location'
    public static final String X_INERTIA_PARTIAL_DATA = 'X-Inertia-Partial-Data'
    public static final String X_INERTIA_PARTIAL_COMPONENT = 'X-Inertia-Partial-Component'

    private final HttpServletRequest request

    InertiaRequest(HttpServletRequest request) {
        this.request = request
    }

    /**
     * Indicates that the request is via an element using X-Inertia
     */
    boolean isInertia() {
        Boolean.parseBoolean(getHeaderValue(X_INERTIA))
    }

    /**
     * Get header value from request
     * @param The name of header
     * @return value
     */
    String getHeaderValue(String name) {
        this.request.getHeader(name)
    }

    boolean asBoolean() {
        isInertia()
    }

}
