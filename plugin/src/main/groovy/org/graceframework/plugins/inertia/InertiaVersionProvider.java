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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import grails.config.Config;
import grails.core.GrailsApplication;

/**
 * Provides asset version for Inertia
 *
 * @author Michael Yan
 * @since 0.5
 */
public class InertiaVersionProvider {

    public static final String BEAN_NAME = "inertiaVersionProvider";

    private GrailsApplication grailsApplication;
    private Config config;

    public InertiaVersionProvider(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
        this.config = grailsApplication.getConfig();
    }

    public String getVersion() {
        String assetVersion = this.config.getProperty(InertiaSettings.INERTIA_ASSET_VERSION);
        if (StringUtils.hasText(assetVersion)) {
            return assetVersion;
        }

        String assetUrl = this.config.getProperty(InertiaSettings.INERTIA_ASSET_URL);
        if (StringUtils.hasText(assetUrl)) {
            String checksum = DigestUtils.md5DigestAsHex(assetUrl.getBytes(StandardCharsets.UTF_8));
            return checksum;
        }

        String manifestLocation = this.config.getProperty(InertiaSettings.INERTIA_MANIFEST_LOCATION);
        if (StringUtils.hasText(manifestLocation)) {
            Resource manifest = this.grailsApplication.getMainContext().getResource(manifestLocation);
            if (manifest.exists()) {
                try {
                    String checksum = DigestUtils.md5DigestAsHex(manifest.getInputStream());
                    return checksum;
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        return "";
    }

}
