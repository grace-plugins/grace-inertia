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

import java.util.List;

import javax.servlet.DispatcherType;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import grails.core.GrailsApplication;
import grails.rest.render.RendererRegistry;

import org.grails.plugins.web.GroovyPagesAutoConfiguration;
import org.grails.plugins.web.mime.MimeTypesConfiguration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Inertia Plugin.
 *
 * @author Michael Yan
 * @since 0.1
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfiguration(before = { MimeTypesConfiguration.class, GroovyPagesAutoConfiguration.class })
public class InertiaAutoConfiguration {

    @Bean
    @ConditionalOnMissingFilterBean
    public FilterRegistrationBean<InertiaRequestFilter> inertiaFilter() {
        InertiaRequestFilter filter = new InertiaRequestFilter();
        FilterRegistrationBean<InertiaRequestFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        registration.setOrder(OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER + 100);
        return registration;
    }

    @Bean
    public InertiaMimeTypeProvider inertiaMimeTypeProvider() {
        return new InertiaMimeTypeProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public InertiaVersionProvider inertiaVersionProvider(ObjectProvider<GrailsApplication> grailsApplicationObjectProvider) {
        return new InertiaVersionProvider(grailsApplicationObjectProvider.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean
    public InertiaPageRendererRegister inertiaPageRendererRegister(
            ObjectProvider<GrailsApplication> grailsApplicationProvider,
            ObjectProvider<InertiaVersionProvider> inertiaVersionProvider,
            ObjectProvider<RendererRegistry> rendererRegistry) {

        InertiaPageRenderer inertiaPageRenderer = new InertiaPageRenderer(
                InertiaPage.class,
                grailsApplicationProvider.getIfAvailable(),
                inertiaVersionProvider.getIfAvailable());

        return new InertiaPageRendererRegister(rendererRegistry.getIfAvailable(), inertiaPageRenderer);
    }

    @Bean
    public InertiaView inertiaView(ApplicationContext applicationContext, InertiaVersionProvider inertiaVersionProvider) {
        return new InertiaView(applicationContext, inertiaVersionProvider);
    }

    @Bean
    public InertiaWebMvcConfigurer inertiaWebMvcConfigurer(InertiaVersionProvider inertiaVersionProvider) {
        return new InertiaWebMvcConfigurer(inertiaVersionProvider);
    }

    /**
     * {@link WebMvcConfigurer} to add Inertia interceptors and return value handlers.
     */
    static class InertiaWebMvcConfigurer implements WebMvcConfigurer {

        private final InertiaVersionProvider inertiaVersionProvider;

        public InertiaWebMvcConfigurer(InertiaVersionProvider inertiaVersionProvider) {
            this.inertiaVersionProvider = inertiaVersionProvider;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new InertiaHandlerInterceptor());
        }

        @Override
        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
            handlers.add(new InertiaPageMethodReturnValueHandler(this.inertiaVersionProvider));
        }

    }

}
