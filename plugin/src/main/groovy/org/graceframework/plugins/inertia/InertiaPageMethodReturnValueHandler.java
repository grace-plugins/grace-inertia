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

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;

/**
 * HandlerMethodReturnValueHandler to handle return value of {@link InertiaPage}.
 *
 * @author Michael Yan
 * @since 0.5
 */
public class InertiaPageMethodReturnValueHandler extends ModelAndViewMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final InertiaVersionProvider inertiaVersionProvider;

    public InertiaPageMethodReturnValueHandler(InertiaVersionProvider inertiaVersionProvider) {
        this.inertiaVersionProvider = inertiaVersionProvider;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return InertiaPage.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String requestUrl = HttpServletRequestExtension.getUrl(servletRequest);

        InertiaPage inertiaPage = (InertiaPage) returnValue;
        if (!StringUtils.hasLength(inertiaPage.getUrl())) {
            inertiaPage.setUrl(requestUrl);
        }
        if (!StringUtils.hasLength(inertiaPage.getVersion())) {
            inertiaPage.setVersion(this.inertiaVersionProvider.getVersion());
        }

        ModelAndView mav = Inertia.render(inertiaPage);
        super.handleReturnValue(mav, returnType, mavContainer, webRequest);
    }

}
