package com.hgq.hystrix;

import com.hgq.feign.ClientFeign;
import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName com.hgq.hystrix.ClientFeignHystrix
 * @Description: TODO
 * @Author: hgq
 * @Date: 2021-10-29 09:19
 * @Version: 1.0
 */
@Component
public class ClientFeignHystrix implements FallbackFactory<ClientFeign> {
    @Override
    public ClientFeign create(Throwable cause) {
        new ClientFeign() {
            @Override
            public String hello(String name) {
                return "降级";
            }

            @Override
            public String test() {
                return null;
            }
        };
        return null;
    }
}
