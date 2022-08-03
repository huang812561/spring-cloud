package com.hgq.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.hgq.util.SentinelBlockHandlerExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-08-01 20:08
 * @since 1.0
 **/
@RestController
@Slf4j
public class HelloController {

    @RequestMapping(value = "hello")
    @SentinelResource(value = "hello", blockHandlerClass = SentinelBlockHandlerExceptionUtil.class, blockHandler = "blockHandler")
    public String hello() {
        int i = (int) (Math.random() * 10);
        if (i % 5 > 4) {
            throw new RuntimeException("自定义随机异常");
        }
        return "hello sentinel service";
    }


    /*public String blockHandler(BlockException e) {
        log.error("被限流", e);
        if (e instanceof FlowException) {//流控异常
            return CodeMsgEnum.FLOW_RULE_EXCEPTION.getMsg();
        } else if (e instanceof DegradeException) {//降级异常
            return CodeMsgEnum.DEGRDE_RULE_EXCEPTION.getMsg();
        } else if (e instanceof ParamFlowException) {//超过系统设置上限
            return CodeMsgEnum.PARAM_FLOW_RULE_EXCEPTION.getMsg();
        } else if (e instanceof SystemBlockException) {//系统负载过高
            return CodeMsgEnum.SYSTEM_RULE_EXCEPTION.getMsg();
        } else if (e instanceof AuthorityException) {
            return CodeMsgEnum.AUTHORITY_RULE_EXCEPTION.getMsg();
        } else {
            return CodeMsgEnum.SENTINEL_COMMON_EXCEPTION.getMsg();
        }
    }*/
}
