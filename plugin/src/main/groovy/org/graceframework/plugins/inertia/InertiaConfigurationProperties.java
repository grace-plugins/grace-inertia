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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Base class for configuration of Inertia.
 *
 * @author Michael Yan
 * @since 0.5
 */
@ConfigurationProperties(prefix = "inertia")
public class InertiaConfigurationProperties {

    /**
     * Whether using inertia is enabled.
     */
    private boolean enabled = true;

    private Asset asset = new Asset();

    private Manifest manifest = new Manifest();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public void setManifest(Manifest manifest) {
        this.manifest = manifest;
    }

    /**
     * Assets
     */
    public static class Asset {

        /**
         * The current version of the assets.
         */
        private String version;

        /**
         * The url of the assets.
         */
        private String url;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    /**
     * Vite manifest file.
     */
    public static class Manifest {

        /**
         * The location of vite manifest file.
         */
        private String location;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

    }

}
