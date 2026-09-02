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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import org.grails.encoder.impl.HTMLEncoder;

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
     * @param request The HttpServletRequest
     * @param component The name of component
     * @return ModelAndView
     */
    public static ModelAndView render(HttpServletRequest request, String component) {
        return render(request, null, component, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * Render Inertia
     *
     * @param request The HttpServletRequest
     * @param component The name of component
     * @param props The page props
     * @return ModelAndView
     */
    public static ModelAndView render(HttpServletRequest request, String component, Map<String, Object> props) {
        return render(request, null, component, props, Collections.emptyMap());
    }

    /**
     * Render Inertia
     *
     * @param request The HttpServletRequest
     * @param inertiaPage The InertiaPage
     * @return ModelAndView
     */
    public static ModelAndView render(HttpServletRequest request, InertiaPage inertiaPage) {
        return render(request, inertiaPage.getVersion(), inertiaPage.getComponent(),
                inertiaPage.getProps(), inertiaPage.getViewData());
    }

    /**
     * Render Inertia
     *
     * @param isInertiaRequest The HttpServletRequest whether is Inertia
     * @param inertiaPage The InertiaPage
     * @return ModelAndView
     */
    public static ModelAndView render(boolean isInertiaRequest, InertiaPage inertiaPage) {
        return render(isInertiaRequest, inertiaPage.getVersion(), inertiaPage.getComponent(),
                inertiaPage.getUrl(), inertiaPage.getProps(), inertiaPage.getViewData());
    }

    /**
     * Render Inertia
     *
     * @param request The HttpServletRequest
     * @param assetVersion The current asset version
     * @param component The name of component
     * @param props The props
     * @param viewData The view data
     * @return ModelAndView
     */
    public static ModelAndView render(HttpServletRequest request, String assetVersion,
            String component, Map<String, Object> props, Map<String, Object> viewData) {
        String url = HttpServletRequestExtension.getUrl(request);
        boolean isInertiaRequest = isInertiaRequest(request);
        return render(isInertiaRequest, assetVersion, component, url, props, viewData);
    }

    /**
     * Render Inertia
     *
     * @param isInertiaRequest The HttpServletRequest is an Inertia request
     * @param assetVersion The current asset version
     * @param component The name of component
     * @param url The page URL
     * @param props The page props
     * @param viewData The view data
     * @return ModelAndView
     */
    public static ModelAndView render(boolean isInertiaRequest, String assetVersion,
            String component, String url, Map<String, Object> props, Map<String, Object> viewData) {
        if (isInertiaRequest) {
            Map<String, Object> pageObject = new LinkedHashMap<>();
            pageObject.put(InertiaPage.COMPONENT, component);
            pageObject.put(InertiaPage.PROPS, props);
            pageObject.put(InertiaPage.URL, url);
            pageObject.put(InertiaPage.VERSION, assetVersion);
            ModelAndView mv = new ModelAndView();
            mv.addAllObjects(pageObject);
            mv.setView(new MappingJackson2JsonView());
            mv.setStatus(HttpStatus.OK);
            return mv;
        }
        else {
            HTMLEncoder encoder = new HTMLEncoder();
            InertiaPage inertiaPage = InertiaPage.of(component, props);
            inertiaPage.setVersion(assetVersion);
            inertiaPage.setUrl(url);

            try {
                ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
                String json = objectMapper.writeValueAsString(inertiaPage);
                String page = encoder.encode(json).toString();
                ModelAndView mv = new ModelAndView("inertia");
                mv.addObject("page", page);
                mv.addAllObjects(viewData);
                return mv;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Check request whether is Inertia or not
     *
     * @param request The HttpServletRequest
     * @return true If the request is Inertia else false
     */
    public static boolean isInertiaRequest(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getHeader(InertiaHeaders.X_INERTIA));
    }

}
