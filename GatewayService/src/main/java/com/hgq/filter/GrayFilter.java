package com.hgq.filter;

import com.hgq.loadbalancer.GrayLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * TODO
 *
 * @Author: hgq
 * @Date: 2021-11-08 19:10
 * @Version: 1.0
 */
public class GrayFilter implements GlobalFilter, Ordered {

    private Logger log = LoggerFactory.getLogger(GrayFilter.class);

    private static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10150;

    private final LoadBalancerClientFactory clientFactory;

    public GrayFilter(LoadBalancerClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        URI url = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);
        //灰度协议
        if (null != url && ("grayLb".equals(schemePrefix) || "grayLb".equals(url.getScheme()))) {
            ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);
            if (log.isTraceEnabled()) {
                log.trace(GrayFilter.class.getSimpleName() + " url before : {}", url);
            }

            return this.choose(exchange).doOnNext((response) -> {
                if (!response.hasServer()) {
                    throw NotFoundException.create(true, "Unable to find instance for " + url.getHost());
                } else {
                    URI uri = exchange.getRequest().getURI();
                    String overideScheme = schemePrefix;
                    if (null != schemePrefix) {
                        overideScheme = url.getScheme();
                    }

                    DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(response.getServer(), overideScheme);
                    URI requestUrl = this.reconstructURI(serviceInstance, uri);
                    if (log.isTraceEnabled()) {
                        log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
                    }
                    exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
                }
            }).then(chain.filter(exchange));
        } else {
            return chain.filter(exchange);
        }
    }

    private URI reconstructURI(DelegatingServiceInstance serviceInstance, URI uri) {
        return LoadBalancerUriTools.reconstructURI(serviceInstance, uri);
    }

    public Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
        URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        GrayLoadBalancer loadBalancer = new GrayLoadBalancer(clientFactory.getLazyProvider(uri.getHost(), ServiceInstanceListSupplier.class), uri.getHost());
        if (null == loadBalancer) {
            throw new NotFoundException("No loadbalance available for " + uri.getHost());
        } else {
            return loadBalancer.choose(this.createRequest(exchange));
        }
    }

    private Request createRequest(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Request<HttpHeaders> request = new DefaultRequest<>(headers);
        return request;
    }


    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }
}
