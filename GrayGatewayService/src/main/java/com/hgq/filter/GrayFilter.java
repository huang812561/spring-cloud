package com.hgq.filter;

import com.hgq.config.CustomConfig;
import com.hgq.loadbalancer.GrayLoadBalancer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * TODO
 *
 * @Author: hgq
 * @Date: 2021-11-09 13:39
 * @Version: 1.0
 */
@Component
public class GrayFilter implements GlobalFilter, Ordered {

    private static final Log log = LogFactory.getLog(GrayFilter.class);
    public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10150;
    private final LoadBalancerClientFactory clientFactory;

    @Autowired
    private CustomConfig customConfig;

    public GrayFilter(LoadBalancerClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        URI url = (URI) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);

        if (customConfig.isGray()) {
            //1. 若自定义需要灰度发布的route的shame,可以使用这个
            String schemePrefix = ((URI) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)).getScheme();
            if (url != null && ("grayLb".equals(url.getScheme()) || "grayLb".equals(schemePrefix))) {
                ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);
                if (log.isTraceEnabled()) {
                    log.trace(ReactiveLoadBalancerClientFilter.class.getSimpleName() + " url before: " + url);
                }
                return doFilter(exchange, chain, url);
            }

            //2.直接走自定义路由规则
            return doFilter(exchange, chain, url);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain, URI url) {
        String schemePrefix = (String) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);

        return this.choose(exchange).doOnNext(((response) -> {
            if (!response.hasServer()) {
                throw NotFoundException.create(true, "Unable to find instance for " + url.getHost());
            } else {
                URI uri = exchange.getRequest().getURI();
                //overrideScheme为空时 转发的路由是http请求
                String overrideScheme = response.getServer().isSecure() ? "https" : "http";
                if (schemePrefix != null) {
                    overrideScheme = url.getScheme();
                }
                DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(response.getServer(), overrideScheme);
                URI requestUrl = this.reconstructURI(serviceInstance, uri);
                if (log.isTraceEnabled()) {
                    log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
                }
                exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);

            }
        })).then(chain.filter(exchange));
    }

    protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
        return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
    }

    private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
        URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        //自定义负载均衡
        GrayLoadBalancer loadBalancer = new GrayLoadBalancer(
                clientFactory.getProvider(uri.getHost(), ServiceInstanceListSupplier.class),
                uri.getHost(),
                customConfig
        );

        if (loadBalancer == null) {
            throw new NotFoundException("No loadbalancer available for " + uri.getHost());
        } else {
            return loadBalancer.choose(this.createRequest(exchange));
        }
    }

    private Request createRequest(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Request<HttpHeaders> request = new DefaultRequest<>(headers);
        return request;
    }

}
