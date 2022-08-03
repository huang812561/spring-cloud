package com.hgq.controller;

import com.hgq.feign.ClientFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    private ClientFeign clientFeign;
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/get/{name}")
    public String get(@PathVariable("name")String name){
        return clientFeign.get(name);
    }


    @RequestMapping("/query/{name}")
    public String query(@PathVariable("name") String name) {
        String returnStr = clientFeign.test();
        System.out.println("A-B result:" + returnStr);
        return "success ->" + returnStr;
    }

    @RequestMapping("/baidu")
    public String baidu() {
        String url = "http://EUREKA-CLIENT/EurekaClient/test";
        String result = clientFeign.test();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        return result;
    }


}
