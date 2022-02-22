package com.hgq.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName com.hgq.config.RestTemplateConfig
 * @Description: TODO
 * @Author: hgq
 * @Date: 2021-10-27 10:18
 * @Version: 1.0
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public RestTemplate loadRestTemplate(){

        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(3L))
                .setReadTimeout(Duration.ofSeconds(2L))
                .build();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        /** 解决中文乱码的converter */
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        messageConverters.add(stringHttpMessageConverter);
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }
}
