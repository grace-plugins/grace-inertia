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

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

/**
 * A trait that adds behavior to allow rendering Inertia page object to the response
 *
 * @author Michael Yan
 * @since 0.5
 */
public class Inertia {

    /**
     * Render Inertia
     *
     * @param component The name of component
     * @return ModelAndView
     */
    public static ModelAndView render(String component) {
        return render(component, null, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * Render Inertia
     *
     * @param component The name of component
     * @param props The page props
     * @return ModelAndView
     */
    public static ModelAndView render(String component, Map<String, Object> props) {
        return render(component, null, props, Collections.emptyMap());
    }

    /**
     * Render Inertia
     *
     * @param inertiaPage The InertiaPage
     * @return ModelAndView
     */
    public static ModelAndView render(InertiaPage inertiaPage) {
        return render(inertiaPage.getComponent(), inertiaPage.getUrl(), inertiaPage.getProps(), inertiaPage.getViewData());
    }

    /**
     * Render Inertia
     *
     * @param component The name of component
     * @param props The page props
     * @param viewData The view data
     * @return ModelAndView
     */
    public static ModelAndView render(String component, Map<String, Object> props, Map<String, Object> viewData) {
        return render(component, null, props, viewData);
    }

    /**
     * Render Inertia
     *
     * @param component The name of component
     * @param url The page URL
     * @param props The page props
     * @param viewData The view data
     * @return ModelAndView
     */
    public static ModelAndView render(String component, String url, Map<String, Object> props, Map<String, Object> viewData) {
        ModelAndView mav = new ModelAndView(InertiaView.INERTIA_VIEW_BEAN_NAME)
                .addObject(InertiaPage.COMPONENT, component)
                .addObject(InertiaPage.URL, url)
                .addObject(InertiaPage.PROPS, props)
                .addAllObjects(viewData);
        return mav;
    }

    /**
     * Check request whether is Inertia or not
     *
     * @param request The HttpServletRequest
     * @return true If the request is Inertia else false
     */
    public static boolean isInertiaRequest(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getHeader(InertiaHeaders.INERTIA));
    }

}
