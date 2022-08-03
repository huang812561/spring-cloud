package com.hgq.entity;

import lombok.Getter;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-08-02 11:36
 * @since 1.0
 **/
@Getter
public enum CodeMsgEnum {
    SYSTEM_DEGRAGE("-2","系统降级!"),
    FAIL("-1","失败!"),
    SUCCESS("0","成功!"),

    FLOW_RULE_EXCEPTION("10001","请求太频繁"),
    DEGRDE_RULE_EXCEPTION("10002","系统繁忙"),
    PARAM_FLOW_RULE_EXCEPTION("10003","超过系统设置上限"),
    AUTHORITY_RULE_EXCEPTION("10004","未授权"),
    SYSTEM_RULE_EXCEPTION("10005","系统负载过高"),
    SENTINEL_COMMON_EXCEPTION("10006","命中限流规则");
    ;
    private String code;
    private String msg;

    CodeMsgEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
