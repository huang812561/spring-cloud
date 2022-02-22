package com.hgq.controller;

import com.hgq.feign.ClientFeign;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.HystrixProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @ClassName com.hgq.controller.HystrixController
 * @Description: TODO
 * @Author: hgq
 * @Date: 2021-10-27 14:32
 * @Version: 1.0
 */
@RestController
@RequestMapping("/hystrix")
public class HystrixController {

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private ClientFeign clientFeign;

    /**
     * 注意：降级逻辑方法要和正常逻辑具有相同的参数列表和返回值声明
     * 降级超时设置方式：
     * 1.可以在注解中直接指定超时时间
     * 2.可以指定commandKey，在yaml/properties中配置相应超时时间
     * 3.可以针对接口级别进行配置
     *
     * @return
     */
/*    @HystrixCommand(fallbackMethod = "errFallBack", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
    })*/
    @RequestMapping("/hello")
    @HystrixCommand(fallbackMethod = "errFallBack", commandKey = "userKey")
    public String hello() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://EUREKA-CLIENT/EurekaClient/hello/张三", String.class);
        String resultStr = responseEntity.getBody();
        return "return hystrix :" + resultStr;
    }

    //默认熔断是1秒 这里设置5秒
    @HystrixCommand(fallbackMethod = "errFallBack2", commandKey = "loginKey")
    @RequestMapping("/hello2/{name}")
    public String hello2(@PathVariable("name") String name) {
        String resultStr = clientFeign.hello(name);
        return "return hystrix :" + resultStr;
    }

    public String errFallBack() {
        return "服务不可用，请稍后重试！";
    }

    public String errFallBack2(String name) {
        return name + "服务不可用，请稍后重试！";
    }
}
