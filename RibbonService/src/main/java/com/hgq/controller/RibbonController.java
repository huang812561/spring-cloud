package com.hgq.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @ClassName com.hgq.controller.RibbonController
 * @Description: TODO
 * @Author: hgq
 * @Date: 2021-10-27 10:16
 * @Version: 1.0
 */
@RestController
@RequestMapping("/ribbon")
public class RibbonController {

    private final Logger log = LoggerFactory.getLogger(RibbonController.class);

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    @Resource(name = "loadRestTemplate")
    private RestTemplate loadRestTemplate;

    @RequestMapping("/hello")
    public String hello() {
        String url = "http://EUREKA-CLIENT:8081/EurekaClient/hello/张三";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String result = responseEntity.getBody();
        System.out.println("A-B result:" + result);
        return "hello ribbon,return :" + result;
    }

    @RequestMapping("/baidu")
    public String baidu() {
        String url = "http://EUREKA-CLIENT:8081/EurekaClient/test";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String result = responseEntity.getBody();
        return "hello ribbon,return :" + result;
    }

    @RequestMapping("test")
    public String test() {
        ResponseEntity<String> responseEntity = loadRestTemplate.getForEntity("http://www.baidu.com", String.class);
        String urlStr = responseEntity.getBody();
        log.info("返回地址：" + urlStr);
        return urlStr;
    }
}
