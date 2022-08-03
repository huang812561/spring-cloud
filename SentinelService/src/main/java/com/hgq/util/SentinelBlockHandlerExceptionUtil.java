package com.hgq.util;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.hgq.entity.CodeMsgEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-08-02 11:32
 * @since 1.0
 **/
@Slf4j
public class SentinelBlockHandlerExceptionUtil {

    /**
     * 此处的方法必须是static，且参数要与请求方法参数一致
     *
     * @param e
     * @return
     */
    public static String blockHandler(BlockException e) {
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
    }

}
