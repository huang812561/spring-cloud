package com.hgq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 是否开启灰度标识
 *
 * @Author: hgq
 * @Date: 2021-11-08 21:45
 * @Version: 1.0
 */
@Configuration
public class CustomConfig {

    @Value("${api.gray.enabled}")
    private boolean isGray = false;

    @Value("${api.gray.default.version}")
    private String defaultVersion;

    public boolean isGray() {
        return isGray;
    }

    public void setGray(boolean gray) {
        isGray = gray;
    }

    public String getDefaultVersion() {
        return defaultVersion;
    }

    public void setDefaultVersion(String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }
}
