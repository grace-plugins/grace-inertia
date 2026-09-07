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

import org.grails.encoder.CodecLookup

/**
 *
 * @author Michael Yan
 * @since 0.5
 */
class InertiaTagLib {

    static String namespace = 'inertia'

    CodecLookup codecLookup

    Closure app = { Map<String, Object> attrs, Closure body ->
        def config = grailsApplication.config
        String id = attrs.id ?: 'app'
        if (config.getProperty(InertiaSettings.INERTIA_INITIAL_PAGE_USE_SCRIPT_ELEMENT, Boolean, false)) {
            String page = getPageAttribute()
            out << "<script data-page=\"app\" type=\"application/json\">$page</script>"
            out << "<div id=\"$id\"></div>"
        }
        else {
            def encoder = codecLookup.lookupEncoder('HTML')
            String page = encoder.encode(getPageAttribute())
            out << "<div id=\"$id\" data-page=\"$page\"></div>"
        }
    }

    private String getPageAttribute() {
        request.getAttribute(InertiaSettings.INERTIA_PAGE_ATTRIBUTE)
    }

}
