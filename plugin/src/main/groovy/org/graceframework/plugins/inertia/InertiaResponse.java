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

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/**
 * See <a href="https://inertiajs.com/the-protocol#inertia-responses">Inertia Response</a>
 *
 * @author Michael Yan
 * @since 0.1
 */
public class InertiaResponse {

    private final HttpHeaders headers;

    private final String component;

    @Nullable
    private final Map<String, Object> props;
    private final Map<String, Object> viewData;
    private final Object status;

    /**
     * Create a {@code InertiaResponse} with a component only.
     * @param component the component
     */
    public InertiaResponse(String component) {
        this(component, HttpStatus.OK);
    }

    /**
     * Create a {@code InertiaResponse} with a status code only.
     * @param component the component
     * @param status the status code
     */
    public InertiaResponse(String component, HttpStatus status) {
        this(component, null, null, null, status);
    }

    /**
     * Create a {@code InertiaResponse} with a body and status code.
     * @param component the component
     * @param props the page props
     * @param status the status code
     */
    public InertiaResponse(String component, @Nullable Map<String, Object> props, HttpStatus status) {
        this(component, props, null, null, status);
    }

    /**
     * Create a {@code InertiaResponse} with headers and a status code.
     * @param component the component
     * @param headers the entity headers
     * @param status the status code
     */
    public InertiaResponse(String component, MultiValueMap<String, String> headers, HttpStatus status) {
        this(component, null, null, headers, status);
    }

    /**
     * Create a {@code InertiaResponse} with a body, headers, and a status code.
     * @param component the component
     * @param props the page props
     * @param viewData the view data
     * @param headers the entity headers
     */
    public InertiaResponse(String component, @Nullable Map<String, Object> props, @Nullable  Map<String, Object> viewData, @Nullable MultiValueMap<String, String> headers) {
        this(component, props, viewData, headers, HttpStatus.OK);
    }

    /**
     * Create a {@code InertiaResponse} with a body, headers, and a status code.
     * @param component the component
     * @param props the page props
     * @param viewData the view data
     * @param headers the entity headers
     * @param status the status code
     */
    public InertiaResponse(String component, @Nullable Map<String, Object> props, @Nullable Map<String, Object> viewData, @Nullable MultiValueMap<String, String> headers, HttpStatus status) {
        this(component, props, null, headers, (Object) status);
    }

    /**
     * Create a {@code InertiaResponse} with a body, headers, and a raw status code.
     * @param component the component
     * @param props the page props
     * @param viewData the view data
     * @param headers the entity headers
     * @param rawStatus the status code value
     */
    public InertiaResponse(String component, @Nullable Map<String, Object> props, @Nullable Map<String, Object> viewData, @Nullable MultiValueMap<String, String> headers, int rawStatus) {
        this(component, props, viewData, headers, (Object) rawStatus);
    }

    /**
     * Private constructor.
     */
    private InertiaResponse(String component, @Nullable Map<String, Object> props, @Nullable Map<String, Object> viewData, @Nullable MultiValueMap<String, String> headers, Object status) {
        Assert.notNull(component, "Component must not be null");
        Assert.notNull(status, "HttpStatus must not be null");
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers != null ? headers : new HttpHeaders());
        this.props = props != null ? props : new LinkedHashMap<>();
        this.component = component;
        this.viewData = viewData != null ? viewData : new LinkedHashMap<>();
        this.status = status;
    }

    public String getComponent() {
        return this.component;
    }

    @Nullable
    public Map<String, Object> getProps() {
        return props;
    }

    public Map<String, Object> getViewData() {
        return this.viewData;
    }

    /**
     * Returns the headers of this entity.
     * @return the headers
     */
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    /**
     * Return the HTTP status code of the response.
     * @return the HTTP status as an HttpStatus enum entry
     */
    public HttpStatus getStatusCode() {
        if (this.status instanceof HttpStatus) {
            return (HttpStatus) this.status;
        }
        else {
            return HttpStatus.valueOf((Integer) this.status);
        }
    }

    /**
     * Return the HTTP status code of the response.
     * @return the HTTP status as an int value
     * @since 4.3
     */
    public int getStatusCodeValue() {
        if (this.status instanceof HttpStatus) {
            return ((HttpStatus) this.status).value();
        }
        else {
            return (Integer) this.status;
        }
    }


    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        InertiaResponse otherResponse = (InertiaResponse) other;
        return ObjectUtils.nullSafeEquals(this.component, otherResponse.component);
    }

    @Override
    public int hashCode() {
        return (29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.component));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        builder.append(this.component);
        builder.append(',');
        builder.append(this.status);
        if (this.status instanceof HttpStatus) {
            builder.append(' ');
            builder.append(((HttpStatus) this.status).getReasonPhrase());
        }
        builder.append(',');
        Map<String, Object> props = getProps();
        HttpHeaders headers = getHeaders();
        if (props != null) {
            builder.append(props);
            builder.append(',');
        }
        builder.append(headers);
        builder.append('>');
        return builder.toString();
    }


    // Static builder methods

    /**
     * Create a builder with the given status.
     * @param component the component
     * @return the created builder
     */
    public static InertiaResponse.BodyBuilder component(String component) {
        Assert.notNull(component, "Component must not be null");
        return new InertiaResponse.DefaultBuilder(component);
    }


    /**
     * Defines a builder that adds headers to the response entity.
     * @param <B> the builder subclass
     */
    public interface HeadersBuilder<B extends InertiaResponse.HeadersBuilder<B>> {

        /**
         * Add the given, single header value under the given name.
         * @param headerName the header name
         * @param headerValues the header value(s)
         * @return this builder
         * @see HttpHeaders#add(String, String)
         */
        B header(String headerName, String... headerValues);

        /**
         * Copy the given headers into the entity's headers map.
         * @param headers the existing HttpHeaders to copy from
         * @return this builder
         * @see HttpHeaders#add(String, String)
         */
        B headers(@Nullable HttpHeaders headers);

        /**
         * Manipulate this entity's headers with the given consumer. The
         * headers provided to the consumer are "live", so that the consumer can be used to
         * {@linkplain HttpHeaders#set(String, String) overwrite} existing header values,
         * {@linkplain HttpHeaders#remove(Object) remove} values, or use any of the other
         * {@link HttpHeaders} methods.
         * @param headersConsumer a function that consumes the {@code HttpHeaders}
         * @return this builder
         */
        B headers(Consumer<HttpHeaders> headersConsumer);

        /**
         * Set the set of allowed {@link HttpMethod HTTP methods}, as specified
         * by the {@code Allow} header.
         * @param allowedMethods the allowed methods
         * @return this builder
         * @see HttpHeaders#setAllow(Set)
         */
        B allow(HttpMethod... allowedMethods);

        /**
         * Set the entity tag of the body, as specified by the {@code ETag} header.
         * @param etag the new entity tag
         * @return this builder
         * @see HttpHeaders#setETag(String)
         */
        B eTag(String etag);

        /**
         * Set the time the resource was last changed, as specified by the
         * {@code Last-Modified} header.
         * @param lastModified the last modified date
         * @return this builder
         * @see HttpHeaders#setLastModified(ZonedDateTime)
         */
        B lastModified(ZonedDateTime lastModified);

        /**
         * Set the time the resource was last changed, as specified by the
         * {@code Last-Modified} header.
         * @param lastModified the last modified date
         * @return this builder
         * @see HttpHeaders#setLastModified(Instant)
         */
        B lastModified(Instant lastModified);

        /**
         * Set the time the resource was last changed, as specified by the
         * {@code Last-Modified} header.
         * <p>The date should be specified as the number of milliseconds since
         * January 1, 1970 GMT.
         * @param lastModified the last modified date
         * @return this builder
         * @see HttpHeaders#setLastModified(long)
         */
        B lastModified(long lastModified);

        /**
         * Set the location of a resource, as specified by the {@code Location} header.
         * @param location the location
         * @return this builder
         * @see HttpHeaders#setLocation(URI)
         */
        B location(URI location);

        /**
         * Set the caching directives for the resource, as specified by the HTTP 1.1
         * {@code Cache-Control} header.
         * <p>A {@code CacheControl} instance can be built like
         * {@code CacheControl.maxAge(3600).cachePublic().noTransform()}.
         * @param cacheControl a builder for cache-related HTTP response headers
         * @return this builder
         * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.2">RFC-7234 Section 5.2</a>
         */
        B cacheControl(CacheControl cacheControl);

        /**
         * Configure one or more request header names (e.g. "Accept-Language") to
         * add to the "Vary" response header to inform clients that the response is
         * subject to content negotiation and variances based on the value of the
         * given request headers. The configured request header names are added only
         * if not already present in the response "Vary" header.
         * @param requestHeaders request header names
         * @return this builder
         */
        B varyBy(String... requestHeaders);

        /**
         * Build the response entity with no body.
         * @return the response entity
         * @see ResponseEntity.BodyBuilder#body(Object)
         */
        InertiaResponse build();
    }


    /**
     * Defines a builder that adds a body to the response entity.
     */
    public interface BodyBuilder extends InertiaResponse.HeadersBuilder<InertiaResponse.BodyBuilder> {

        /**
         * Set the view data.
         *
         * @param viewData the view data
         * @return this builder
         */
        InertiaResponse.BodyBuilder viewData(Map<String, Object> viewData);

        /**
         * Set the view data.
         *
         * @param key the key of view data
         * @param value the value of view data
         * @return this builder
         */
        InertiaResponse.BodyBuilder viewData(String key, Object value);

        /**
         * Set the status code.
         *
         * @param statusCode the response status
         * @return this builder
         */
        InertiaResponse.BodyBuilder statusCode(Object statusCode);

        /**
         * Set the length of the body in bytes, as specified by the
         * {@code Content-Length} header.
         * @param contentLength the content length
         * @return this builder
         * @see HttpHeaders#setContentLength(long)
         */
        InertiaResponse.BodyBuilder contentLength(long contentLength);

        /**
         * Set the {@linkplain MediaType media type} of the body, as specified by the
         * {@code Content-Type} header.
         * @param contentType the content type
         * @return this builder
         * @see HttpHeaders#setContentType(MediaType)
         */
        InertiaResponse.BodyBuilder contentType(MediaType contentType);

        /**
         * Set the page props of the page object and returns it.
         * @param props the page props
         * @return the built response entity
         */
        InertiaResponse props(@Nullable Map<String, Object> props);
    }


    private static class DefaultBuilder implements InertiaResponse.BodyBuilder {

        private final String component;
        private final Map<String, Object> viewData = new LinkedHashMap<>();
        private Object statusCode;

        private final HttpHeaders headers = new HttpHeaders();

        public DefaultBuilder(String component) {
            this.component = component;
            this.statusCode = HttpStatus.OK;
        }

        @Override
        public InertiaResponse.BodyBuilder viewData(Map<String, Object> viewData) {
            if (viewData != null) {
                this.viewData.putAll(viewData);
            }
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder viewData(String key, Object value) {
            this.viewData.put(key, value);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder statusCode(Object statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder header(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder headers(@Nullable HttpHeaders headers) {
            if (headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(this.headers);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder allow(HttpMethod... allowedMethods) {
            this.headers.setAllow(new LinkedHashSet<>(Arrays.asList(allowedMethods)));
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder contentLength(long contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder eTag(String etag) {
            if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
                etag = "\"" + etag;
            }
            if (!etag.endsWith("\"")) {
                etag = etag + "\"";
            }
            this.headers.setETag(etag);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder lastModified(ZonedDateTime date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder lastModified(Instant date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder lastModified(long date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder location(URI location) {
            this.headers.setLocation(location);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder cacheControl(CacheControl cacheControl) {
            this.headers.setCacheControl(cacheControl);
            return this;
        }

        @Override
        public InertiaResponse.BodyBuilder varyBy(String... requestHeaders) {
            this.headers.setVary(Arrays.asList(requestHeaders));
            return this;
        }

        @Override
        public InertiaResponse build() {
            return props(null);
        }

        @Override
        public InertiaResponse props(@Nullable Map<String, Object> props) {
            return new InertiaResponse(this.component, props, this.viewData, this.headers, this.statusCode);
        }

    }

}
