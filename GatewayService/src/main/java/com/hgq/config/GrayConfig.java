package com.hgq.config;

import com.hgq.filter.GrayFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 灰度拦截器配置
 *
 * @Author: hgq
 * @Date: 2021-11-08 20:30
 * @Version: 1.0
 */
@Configuration
public class GrayConfig {

    @Bean
    @ConditionalOnMissingBean({GrayFilter.class})
    public GrayFilter grayFilter(LoadBalancerClientFactory clientFactory) {
        return new GrayFilter(clientFactory);
    }
}
