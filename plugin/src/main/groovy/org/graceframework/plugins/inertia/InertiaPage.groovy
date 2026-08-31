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

import groovy.transform.builder.Builder

/**
 *
 * @author Michael Yan
 * @since 0.5
 */
@Builder
class InertiaPage {

    public static final String INERTIA = "inertia"
    public static final String COMPONENT = 'component'
    public static final String PROPS = 'props'
    public static final String URL = 'url'
    public static final String VERSION = 'version'
    public static final String VIEW_DATA = "viewData"

    /**
     * The name of the JavaScript page component.
     */
    String component

    /**
     * The page props. Contains all of the page data along with an errors object (defaults to {} if there are no errors).
     */
    Map<String, Object> props

    /**
     * The page URL.
     */
    String url

    /**
     * The current asset version.
     */
    String version

    Map<String, Object> viewData

    InertiaPage(String component) {
        this(component, Collections.EMPTY_MAP)
    }

    InertiaPage(String component, Map<String, Object> props) {
        this.component = component
        this.props = props
    }

    InertiaPage viewData(Map viewData) {
        this.viewData = viewData
        this
    }

    static InertiaPage of(String component, Map props) {
        new InertiaPage(component, props)
    }

    static InertiaPage of(Map props) {
        new InertiaPage(null, props)
    }

    static InertiaPage props(Map props) {
        new InertiaPage(null, props)
    }

}
