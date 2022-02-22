package com.hgq.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName com.hgq.config.RibbonConfig
 * @Description: 负载均衡配置类
 * @Author: hgq
 * @Date: 2021-10-27 14:31
 * @Version: 1.0
 */
@Configuration
public class RibbonConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
