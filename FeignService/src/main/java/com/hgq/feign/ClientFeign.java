package com.hgq.feign;

import com.hgq.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName com.hgq.ClientFeign
 * @Description: feign接口
 * @Author: hgq
 * @Date: 2021-10-26 18:09
 * @Version: 1.0
 */
@FeignClient(value = "EUREKA-CLIENT",name = "EUREKA-CLIENT",contextId = "EUREKA-CLIENT",  path = "/EurekaClient", configuration = {FeignConfig.class})
public interface ClientFeign {

    @GetMapping("get")
    String get(@RequestParam("name") String name);

    @PostMapping("test")
    String test();


}
