package com.hgq.loadBalancer;

import com.hgq.entity.WeightMeta;
import com.hgq.utils.WeightRandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义负载均衡
 *
 * @Author hgq
 * @Date: 2022-07-21 18:09
 * @since 1.0
 **/
@Slf4j
public class CustomLoadBalancer implements ReactorLoadBalancer {

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private String serviceId;

    final AtomicInteger position;

    public CustomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId, AtomicInteger position) {
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.serviceId = serviceId;
        this.position = position;
    }

    /**
     * 自定义负载均衡
     *
     * @param request
     * @return
     */
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        HttpHeaders headers = (HttpHeaders) request.getContext();
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances, headers));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier, List<ServiceInstance> serviceInstances, HttpHeaders headers) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances, headers);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, HttpHeaders headers) {
        if (instances.isEmpty()) {
            return getServiceInstanceEmptyResponse();
        }
        //核心逻辑：自定义负载均衡算法
        /*随机算法*/
        /*随机权重算法*/
        /*轮询算法*/
        /*轮询权重算法*/
        /*平滑轮询加权*/
        /*一致性哈希算法*/
        /*最少活跃数*/
        /*权重-路由*/
        getServiceInstanceResponseWithWeight(instances);
        /*版本-路由*/
        getServiceInstanceResponseByVersion(instances, headers);
        return null;
    }

    /**
     * 默认空实例
     *
     * @return
     */
    private Response<ServiceInstance> getServiceInstanceEmptyResponse() {
        log.warn("No servers available for service: " + this.serviceId);
        return new EmptyResponse();
    }

    /**
     * 根据在nacos中配置的权重值，进行分发
     *
     * @param instances
     * @return
     */
    private Response<ServiceInstance> getServiceInstanceResponseWithWeight(List<ServiceInstance> instances) {
        Map<ServiceInstance, Integer> weightMap = new HashMap<>();
        for (ServiceInstance instance : instances) {
            Map<String, String> metadata = instance.getMetadata();
            log.info("With weight metadata version: {},weight: {}", metadata.get("version"), metadata.get("weight"));
            if (metadata.containsKey("weight")) {
                weightMap.put(instance, Integer.valueOf(metadata.get("weight")));
            }
        }
        WeightMeta<ServiceInstance> weightMeta = WeightRandomUtils.buildWeightMeta(weightMap);
        if (ObjectUtils.isEmpty(weightMeta)) {
            return getServiceInstanceEmptyResponse();
        }
        ServiceInstance serviceInstance = weightMeta.random();
        if (ObjectUtils.isEmpty(serviceInstance)) {
            return getServiceInstanceEmptyResponse();
        }
        log.info("service instance get metadata version : {}", serviceInstance.getMetadata().get("version"));
        return new DefaultResponse(serviceInstance);
    }

    /**
     * 根据版本进行分发
     *
     * @param instances
     * @param headers
     * @return
     */
    private Response<ServiceInstance> getServiceInstanceResponseByVersion(List<ServiceInstance> instances, HttpHeaders headers) {
        String versionNo = headers.getFirst("version");
        System.out.println(versionNo);
        Map<String, String> versionMap = new HashMap<>();
        versionMap.put("version", versionNo);
        final Set<Map.Entry<String, String>> attributes =
                Collections.unmodifiableSet(versionMap.entrySet());
        ServiceInstance serviceInstance = null;
        for (ServiceInstance instance : instances) {
            Map<String, String> metadata = instance.getMetadata();
            if (metadata.entrySet().containsAll(attributes)) {
                serviceInstance = instance;
                break;
            }
        }

        if (ObjectUtils.isEmpty(serviceInstance)) {
            return getServiceInstanceEmptyResponse();
        }
        return new DefaultResponse(serviceInstance);
    }
}
