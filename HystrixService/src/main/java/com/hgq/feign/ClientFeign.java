package com.hgq.feign;

import com.hgq.hystrix.ClientFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName com.hgq.ClientFeign
 * @Description: TODO
 * @Author: hgq
 * @Date: 2021-10-26 18:09
 * @Version: 1.0
 */
@FeignClient(name = "EUREKA-CLIENT/EurekaClient",fallbackFactory = ClientFeignHystrix.class)
public interface ClientFeign {

    @RequestMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name);

    @RequestMapping("test")
    public String test();
}
