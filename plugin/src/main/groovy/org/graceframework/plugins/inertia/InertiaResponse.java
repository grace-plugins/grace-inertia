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

import javax.servlet.http.HttpServletResponse;

/**
 * See <a href="https://inertiajs.com/the-protocol#inertia-responses">Inertia Response</a>
 *
 * @author Michael Yan
 * @since 0.1
 */
public class InertiaResponse {

    private final HttpServletResponse response;

    public InertiaResponse(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * The server may immediately return a 409 Conflict response if the asset versions are different, and includes the URL in a X-Inertia-Location header.
     * This header is necessary, since server-side redirects may have occurred. This tells Inertia what the final intended destination URL is.
     *
     * @param location The location header value
     */
    public void setLocation(String location) {
        setHeaderValue(InertiaHeaders.INERTIA_LOCATION, location);
    }

    public void setHeaderValue(String name, Object value) {
        this.response.setHeader(name, (value == null) ? "" : value.toString());
    }

}
