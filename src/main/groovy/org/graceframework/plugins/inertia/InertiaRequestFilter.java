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
package org.graceframework.plugins.inertia;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import grails.web.mime.MimeType;
import org.grails.web.util.GrailsApplicationAttributes;

/**
 * {@link OncePerRequestFilter} to check current request whether from Inertia or not,
 * and will set attribute content and response format.
 *
 * @author Michael Yan
 * @since 0.1
 */
public class InertiaRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        request.setAttribute(GrailsApplicationAttributes.CONTENT_FORMAT, InertiaMimeType.INERTIA_FORMAT);
        request.setAttribute(GrailsApplicationAttributes.RESPONSE_FORMAT, InertiaMimeType.INERTIA_FORMAT);
        request.setAttribute(GrailsApplicationAttributes.RESPONSE_MIME_TYPE, MimeType.JSON);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !Boolean.parseBoolean(request.getHeader(InertiaRequest.X_INERTIA));
    }

}
