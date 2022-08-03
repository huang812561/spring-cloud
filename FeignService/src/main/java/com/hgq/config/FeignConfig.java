package com.hgq.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName com.hgq.config.FeignConfig
 * @Description: 自定义Feign配置的类
 * @Author: hgq
 * @Date: 2021-10-26 19:41
 * @Version: 1.0
 */
//注意：此处如果加了@Configuration，默认全局生效，如果指定对应微服务上，就不能加此注解
//@Configuration
public class FeignConfig {


    /**
     * 设置日志级别两种方式
     * 1.代码配置
     * 2.yaml配置
     * <p>
     * 1.代码实现如下：
     * 自定义日志4种级别介绍
     * NONE 【性能最佳，适用于生产】：不记录任何日志（默认值）。
     * BASIC【适用于生产环境追踪问题】：仅记录请求方法、URL、响应状态码以及执行时间。
     * HEADERS：在BASIC级别的基础上，记录请求和响应的header。
     * FULL【适用于开发及测试环境定位问题】：记录请求和响应的header、body和元数据。
     * 2.yaml配置
     * feign:
     * client:
     * config:
     * service-client1: # 对应服务名
     * loggerLevel: BASIC
     *
     * @return
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    /**
     * 增加拦截器
     *
     * @return
     */
/*    @Bean
    public RequestInterceptor interceptor() {
        return new FeignRequestInterceptor();
    }*/


/*
    @Bean
    public Feign.Builder feignBuilder(@Qualifier("client") Client client) {
        final Client trustSSLSockets = client;
        return Feign.builder().client(trustSSLSockets);
    }

    @Bean
    public Client client(){
        return new Client.Default(
                TrustingSSLSocketFactory.get(), new NoopHostnameVerifier());
    }
*/



}
