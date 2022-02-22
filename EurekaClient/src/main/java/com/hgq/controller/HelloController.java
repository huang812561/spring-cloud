package com.hgq.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.ws.rs.POST;

/**
 * @ClassName com.hgq.controller.HelloController
 * @Description: TODO
 * @Author: hgq
 * @Date: 2021-10-25 18:05
 * @Version: 1.0
 */
@RestController
@RequestMapping("/")
public class HelloController {

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name) {
        System.out.println("8088");
        return "hello " + name + " !";
    }

    @PostMapping("test")
    public String test() {
        String resultStr = restTemplate.getForObject("http://www.baidu.com", String.class);
        System.out.println(resultStr);
        return resultStr;
    }
}
