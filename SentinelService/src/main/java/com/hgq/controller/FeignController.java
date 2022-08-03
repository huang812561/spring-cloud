package com.hgq.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.hgq.feign.EurekaClientFeign;
import com.hgq.util.SentinelBlockHandlerExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Path;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-08-02 14:33
 * @since 1.0
 **/
@RestController
public class FeignController {

    @Autowired
    private EurekaClientFeign eurekaClientFeign;

    /**
     * QPS测试、并发数测试
     * @return
     */
    @SentinelResource(value = "test",blockHandlerClass = SentinelBlockHandlerExceptionUtil.class,blockHandler = "blockHandler")
    @RequestMapping("/test")
    public String test(){
        String resultStr = eurekaClientFeign.test();
        return resultStr;
    }

    /**
     * 热点测试
     * @param name
     * @return
     */
    @SentinelResource(value = "test",blockHandlerClass = SentinelBlockHandlerExceptionUtil.class,blockHandler = "blockHandler")
    @RequestMapping("/test2/{name}")
    public String test2(@PathVariable("name")String name){
        String resultStr = eurekaClientFeign.get(name);
        return resultStr;
    }
}
