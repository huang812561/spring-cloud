package com.hgq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class EurekaClientApplication {


    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }




    /**
     * 解决RestTempalte中文乱码问题
     *
     * @return
     */
    @Primary
    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(3L))
                .setReadTimeout(Duration.ofSeconds(2L))
                .build();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        /** 解决中文乱码的converter */
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        messageConverters.add(stringHttpMessageConverter);
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    @Bean
    @LoadBalanced
    public RestTemplate loadRestTemplate(){
        return new RestTemplate();
    }
}
