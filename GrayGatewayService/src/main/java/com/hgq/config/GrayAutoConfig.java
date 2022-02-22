package com.hgq.config;

import com.hgq.filter.GrayFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置自定义灰度filter,交由spring管理
 *
 * @Author: hgq
 * @Date: 2021-11-09 19:18
 * @Version: 1.0
 */
@Configuration
public class GrayAutoConfig {

    @Bean
    @ConditionalOnMissingBean({GrayFilter.class})
    public GrayFilter grayFilter(LoadBalancerClientFactory clientFactory) {
        return new GrayFilter(clientFactory);
    }

}
