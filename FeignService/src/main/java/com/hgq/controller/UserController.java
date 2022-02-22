package com.hgq.controller;

import com.hgq.feign.ClientFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @ClassName com.hgq.controller.UserController
 * @Description: TODO
 * @Author: hgq
 * @Date: 2021-10-26 18:03
 * @Version: 1.0
 */
@RestController
public class UserController {

    @Resource
    private ClientFeign client;
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/query")
    public String query() {
        String returnStr = client.hello("zhangsan");
        System.out.println("A-B result:" + returnStr);
        return "success ->" + returnStr;
    }

    @RequestMapping("/baidu")
    public String baidu() {
        String url = "http://EUREKA-CLIENT/EurekaClient/test";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url,String.class);
        String result = responseEntity.getBody();
        return result;
    }



}
