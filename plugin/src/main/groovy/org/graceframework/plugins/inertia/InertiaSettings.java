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

/**
 * Settings for Inertia
 *
 * @author Michael Yan
 * @since 0.5
 */
public final class InertiaSettings {

    public static final String INERTIA_ENABLED = "inertia.enabled";
    public static final String INERTIA_ASSET_URL = "inertia.asset.url";
    public static final String INERTIA_ASSET_VERSION = "inertia.asset.version";
    public static final String INERTIA_MANIFEST_LOCATION = "inertia.manifest.location";
    public static final String INERTIA_MANIFEST_OBJECT = "inertiaManifest";
    public static final String INERTIA_TEMPLATE = "inertia.template";
    public static final String INERTIA_TEMPLATE_DEFAULT = "/templates/inertia";
    public static final String INERTIA_PAGE_ATTRIBUTE = "grails.inertia.page.attribute";
    public static final String INERTIA_SSR_TEMPLATE = "inertia.ssr.template";
    public static final String INERTIA_SSR_TEMPLATE_DEFAULT = "/templates/inertia_ssr";
    public static final String INERTIA_SSR_ENABLED = "inertia.ssr.enabled";
    public static final String INERTIA_SSR_URL = "inertia.ssr.url";
    public static final String INERTIA_INITIAL_PAGE_USE_SCRIPT_ELEMENT = "inertia.initial-page.use-script-element";
    public static final String INERTIA_INITIAL_PAGE_ROOT_DOM_ID = "inertia.initial-page.root-dom-id";

}
