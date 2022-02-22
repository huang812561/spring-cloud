package com.hgq.config;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @ClassName com.hgq.config.HystrixConfig
 * @Description: TODO
 * @Author: hgq
 * @Date: 2021-11-06 16:00
 * @Version: 1.0
 */
@Component
public class HystrixConfig {

    @Bean
    public ServletRegistrationBean getServlet(){
        HystrixMetricsStreamServlet hystrixMetricsStreamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(hystrixMetricsStreamServlet);

        servletRegistrationBean.setLoadOnStartup(1);
        servletRegistrationBean.setUrlMappings(Arrays.asList("/hystrix.stream"));
        servletRegistrationBean.setName("HystrixMetricsStreamServlet");
        return servletRegistrationBean;
    }
}
