package com.hgq.util;

import lombok.extern.slf4j.Slf4j;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-08-02 11:43
 * @since 1.0
 **/
@Slf4j
public class SentinelFallbackException {

    public String fallback(Throwable throwable){
        log.error("服务降级了",throwable);
        return "服务降级 fallback";
    }
}
