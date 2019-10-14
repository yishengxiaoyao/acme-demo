package com.edu.cat.ui.config;

import com.edu.cat.ui.interceptor.CatRestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class AcmeFinancialUIConfig {
    //注入resttemplate bean
    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 保存和传递调用链上下文
        restTemplate.setInterceptors(Collections.singletonList(new CatRestInterceptor()));

        return restTemplate;
    }
}
