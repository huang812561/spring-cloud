package com.hgq.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hgq.web.ParameterBodyReaderHttpServletRequestWrapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Feign自定义拦截器 实现 RequestInterceptor
 * 做A服务时需要通过Feign调用B服务。但是我们的B服务做了某些权限验证。需要验证header或者token什么的。如果某没有token，会被阻止调用
 *
 * @Author: hgq
 * @Date: 2021-11-11 15:22
 * @Version: 1.0
 */
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            /**
             * 获取当前线程的 servletRequestAttributes 对象（是一个ThreadLocal对象，因此是线程隔离的）
             * 如果 你是异步线程调用feign接口，那么你获得的servletRequestAttributes对象默认是null的
             * 可以在开启异步线程前加上如下代码，设置requestAttributes对象可以在父子线程间共享 默认情况下，SpringMvc中requestAttributes是不能在父子线程中共享的
             * RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);
             */
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            // 获取当前线程 老请求的httpServletRequest
            HttpServletRequest originHttpServletRequest = servletRequestAttributes.getRequest();

            if (originHttpServletRequest != null) {
                // 1. 从原请求的request对象中拿到header中的authorization信息
                String authorization = originHttpServletRequest.getHeader("authorization");
                /*第二种：获取header信息*/
                Map<String, String> headers = getHeaders(originHttpServletRequest);
                authorization = headers.get("authorization");
                // 为requestTemplate这个新的请求对象 传递 用户信息请求头
                requestTemplate.header("Authorization", authorization);

                //2. 从原请求的body对象中拿到bodyStr参数信息
                JSONObject parameterJson = JSON.parseObject((new ParameterBodyReaderHttpServletRequestWrapper(originHttpServletRequest)).getBodyString(originHttpServletRequest));
                //为requestTemplate这个新的请求对象 传递 token或其他参数
                if (null != parameterJson && parameterJson.containsKey("header")) {
                    String headerJson = parameterJson.get("header").toString();
                    String token = JSONObject.parseObject(headerJson).getString("token");
                    requestTemplate.header("token", new String[]{token});
                }
            }
        } catch (Exception e) {
            log.error("封装请求头信息失败", e);
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String key = headerNames.nextElement().toLowerCase();
            String value = request.getHeader(key);
            map.put(key,value);
        }
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement().toLowerCase();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
