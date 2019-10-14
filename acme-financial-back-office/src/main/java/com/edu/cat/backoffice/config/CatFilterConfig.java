package com.edu.cat.backoffice.config;

import com.edu.cat.backoffice.filter.CatServletFilter;
import com.edu.cat.backoffice.interceptor.CatRestInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

@Component
public class CatFilterConfig {
    @Bean
    public FilterRegistrationBean catFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CatServletFilter filter = new CatServletFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("cat-filter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(2000);
        clientHttpRequestFactory.setReadTimeout(3000);
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        // 保存和传递调用链上下文
        restTemplate.setInterceptors(Collections.singletonList(new CatRestInterceptor()));

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override public boolean hasError(ClientHttpResponse response)
                    throws IOException {
                try {
                    return super.hasError(response);
                } catch (Exception e) {
                    return true;
                }
            }

            @Override public void handleError(ClientHttpResponse response)
                    throws IOException {
                try {
                    super.handleError(response);
                } catch (Exception e) {
                    throw e;
                }
            }
        });
        return restTemplate;
    }
}
