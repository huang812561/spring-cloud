package com.hgq.feign;

import com.hgq.feign.fallback.EurekaClientFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-08-02 14:31
 * @since 1.0
 **/
@FeignClient(contextId = "EUREKA-CLIENT", name = "EUREKA-CLIENT", path = "/EurekaClient", fallbackFactory = EurekaClientFeignFallback.class)
public interface EurekaClientFeign {
    @PostMapping("test")
    String test();

    @RequestMapping("/get")
    String get(@RequestParam("name") String name);
}