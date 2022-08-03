package com.hgq.feign.fallback;

import com.hgq.feign.EurekaClientFeign;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-08-02 15:18
 * @since 1.0
 **/
@Component
public class EurekaClientFeignFallback implements FallbackFactory<EurekaClientFeign> {
    @Override
    public EurekaClientFeign create(Throwable cause) {
        return new EurekaClientFeign() {
            @Override
            public String test() {
                return "feign test熔断异常";
            }

            @Override
            public String get(String name) {
                return "feign hello 熔断异常";
            }
        };
    }
}
