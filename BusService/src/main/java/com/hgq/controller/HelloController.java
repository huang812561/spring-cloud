package com.hgq.controller;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-06-28 15:43
 * @since 1.0
 **/
@RestController
public class HelloController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendQueue/{message}/{count}")
    public String sendQueue(@PathVariable("message") String message, @PathVariable("count") int count) {
        System.out.println("###########开始向队列发送一条消息##################");

        for (int i = 0; i < count; i++) {
            rabbitTemplate.convertAndSend("order", "发送第 " + i + " 条MQ 消息内容:" + message);
        }

//        rabbitTemplate.send(new Message(message.getBytes(StandardCharsets.UTF_8)));
        rabbitTemplate.send("order", new Message(message.getBytes(StandardCharsets.UTF_8)));

        System.out.println("###########消息发送成功##################");
        return "消息发送成功";

    }
}
