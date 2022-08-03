package com.hgq.consumer;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * rabbitMQ 消息订阅
 *
 * @Author hgq
 * @Date: 2022-06-28 16:38
 * @since 1.0
 **/
@Component
public class RabbitMqConsumer {

    /**
     * 1. 手动创建:
     * 需在RabbitMQ中手动创建 order 队列，否则报错
     * 例如：@RabbitListener(queues = "order",concurrency = "10") ，concurrency是消费线程数
     * 2. 自动创建：
     *
     * @param body
     * @param headers
     * @RabbitListener(queuesToDeclare = @Queue(name = "order"), concurrency = "10")
     * 3. 自动创建队列，Exchange 与 Queue绑定
     * @RabbitListener(bindings = @QueueBinding(value = @Queue(“order”), exchange = @Exchange(“testExChange”)))
     */
//    @RabbitListener(queues = "order",concurrency = "10")
    @RabbitListener(queuesToDeclare = @Queue(name = "order"), concurrency = "10")
    @RabbitListener(bindings = @QueueBinding(value = @Queue("order"), exchange = @Exchange("testExChange")), concurrency = "10")
    public void processMessage1(@Payload String body, @Headers Map<String, Object> headers) {
        System.out.println("body：" + body);
        System.out.println("Headers：" + headers);
    }


/*    @RabbitListener(queues = "order")
    public void processMessage1(@Payload String body, @Header String token) {
        System.out.println("body：" + body);
        System.out.println("token：" + token);
    }*/

/*    @RabbitListener(queues = "order")
    public void receive(Message message, Channel channel) throws IOException {
        System.out.println("receive=" + message.getPayload());
        channel.basicAck(((Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG)), true);
    }*/

/*
    @RabbitListener(queues = "order", concurrency = "10")
    public void receive2(Message message, Channel channel) throws IOException {
        System.out.println("receive2 = " + message.getPayload() + "------->" + Thread.currentThread().getName());
        channel.basicReject(((Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG)), true);
    }*/
}
