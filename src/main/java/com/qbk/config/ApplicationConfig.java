package com.qbk.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 框架统一配置文件
 */
@Configuration
public class ApplicationConfig {

    @Configuration
    @Data
    public static class LogConfig {
        /**
         * 配置关键字:请求日志开关
         */
        @Value("${log.enable:true}")
        private boolean requestEnable;


        /**
         * 配置关键字:请求日志输出长度
         */
        @Value("${log.maxLength:10240}")
        private int requestMaxLength;
    }

}
