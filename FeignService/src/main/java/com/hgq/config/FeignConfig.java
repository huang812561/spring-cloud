package com.hgq.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName com.hgq.config.FeignConfig
 * @Description: TODO
 * @Author: hgq
 * @Date: 2021-10-26 19:41
 * @Version: 1.0
 */
@Configuration
public class FeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
