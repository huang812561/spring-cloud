package com.hgq.loadbalancer;

import com.hgq.config.CustomConfig;
import com.hgq.entity.WeightMeta;
import com.hgq.util.WeightRandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义灰度发布的负载均衡，提供了按照版本和权重的方式
 *
 * @Author: hgq
 * @Date: 2021-11-09 18:31
 * @Version: 1.0
 */
public class GrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private Logger log = LoggerFactory.getLogger(GrayLoadBalancer.class);

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    final AtomicInteger position;
    private static String serviceId;
    private static final String VERSION = "version";
    private static CustomConfig config;

    public GrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> provider, String serviceId, CustomConfig config) {
        this.serviceInstanceListSupplierProvider = provider;
        this.serviceId = serviceId;
        this.position = new AtomicInteger(new Random().nextInt(1000));
        this.config = config;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        HttpHeaders headers = (HttpHeaders) request.getContext();
        if (null != this.serviceInstanceListSupplierProvider) {
            ServiceInstanceListSupplier supplier = this.serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
            return supplier.get().next().map(instanceList -> getInstanceResponse(instanceList, headers));
        }
        return null;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, HttpHeaders headers) {
        if (instances.isEmpty()) {
            return getServiceInstanceEmptyResponse();
        } else {
            //根据版本过滤对应的服务
            return getServiceInstanceEmptyResponseByVersion(instances, headers);
        }
    }

    /**
     * 自定义-版本返回实例
     *
     * @param instances
     * @param headers
     * @return
     */
    private Response<ServiceInstance> getServiceInstanceEmptyResponseByVersion(List<ServiceInstance> instances, HttpHeaders headers) {

        String versionNo = headers.getFirst(VERSION);
        log.debug("request head version :{}", versionNo);

        Map<String, String> versionMap = new HashMap<>();
        versionMap.put("version", versionNo);
        final Set<Map.Entry<String, String>> attributes =
                Collections.unmodifiableSet(versionMap.entrySet());

        ServiceInstance serviceInstance = null;
        List<ServiceInstance> defaultRoundInstanceList = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            Map<String, String> metadataMap = instance.getMetadata();
            if (null != metadataMap) {
                //查找与metadata-version匹配的服务
                if (metadataMap.entrySet().containsAll(attributes)) {
                    serviceInstance = instance;
                    break;
                }
                /**
                 * V2默认为灰度版本，不纳入默认版本轮询范围内
                 */
                if (null != metadataMap.get(VERSION) && metadataMap.get(VERSION).equals(config.getDefaultVersion())) {
                    continue;
                }
            }
            defaultRoundInstanceList.add(instance);
        }
        //如果找不到，则默认取第一个
        if (null == serviceInstance) {
            if (CollectionUtils.isEmpty(instances)) {
                return getServiceInstanceEmptyResponse();
            }
            //无对应版本则默认版本中进行轮询
            return getRoundInstanceResponse(defaultRoundInstanceList);
        }
        return new DefaultResponse(serviceInstance);
    }

    /**
     * 默认返回空实例
     *
     * @return
     */
    private Response<ServiceInstance> getServiceInstanceEmptyResponse() {
        log.warn("No servers available for service: " + this.serviceId);
        return new EmptyResponse();
    }


    /**
     * 轮询-返回实例
     * @param instances
     * @return
     */
    private Response<ServiceInstance> getRoundInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }
        // TODO: enforce order?
        int pos = Math.abs(this.position.incrementAndGet());

        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }


    /**
     * 权重-返回实例
     *
     * @param instances
     * @return
     */
    private Response<ServiceInstance> getServiceInstanceResponseWithWeight(List<ServiceInstance> instances) {
        Map<ServiceInstance, Integer> weightMap = new HashMap<>();
        for (ServiceInstance instance : instances) {
            Map<String, String> metadata = instance.getMetadata();
            System.out.println(metadata.get("version") + "-->weight:" + metadata.get("weight"));
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
        System.out.println(serviceInstance.getMetadata().get("version"));
        return new DefaultResponse(serviceInstance);
    }
}
