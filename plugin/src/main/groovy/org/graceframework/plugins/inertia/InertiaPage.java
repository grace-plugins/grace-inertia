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

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Inertia page object.
 *
 * @author Michael Yan
 * @since 0.5
 */
@JsonIgnoreProperties(value = { "viewData" })
public class InertiaPage {

    public static final String INERTIA = "inertia";
    public static final String COMPONENT = "component";
    public static final String PROPS = "props";
    public static final String URL = "url";
    public static final String VERSION = "version";
    public static final String VIEW_DATA = "viewData";

    /**
     * The name of the JavaScript page component.
     */
    private String component;

    /**
     * The page props. Contains all the page data along with an errors object (defaults to {} if there are no errors).
     */
    private Map<String, Object> props;

    /**
     * The page URL.
     */
    private String url;

    /**
     * The current asset version.
     */
    private String version;

    /**
     * The view data.
     */
    private Map<String, Object> viewData;

    public InertiaPage(String component) {
        this(component, Collections.emptyMap());
    }

    public InertiaPage(String component, Map<String, Object> props) {
        this.component = component;
        this.props = props;
    }

    public InertiaPage url(String url) {
        this.url = url;
        return this;
    }

    public InertiaPage version(String version) {
        this.version = version;
        return this;
    }

    public InertiaPage props(Map<String, Object> props) {
        this.props = props;
        return this;
    }

    public InertiaPage viewData(Map<String, Object> viewData) {
        this.viewData = viewData;
        return this;
    }

    public String getComponent() {
        return this.component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Map<String, Object> getProps() {
        return this.props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getViewData() {
        return this.viewData;
    }

    public void setViewData(Map<String, Object> viewData) {
        this.viewData = viewData;
    }

    public static InertiaPage of(String component) {
        return new InertiaPage(component, Collections.emptyMap());
    }

    public static InertiaPage of(String component, Map<String, Object> props) {
        return new InertiaPage(component, props);
    }

    public String toJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(this);
    }

}
