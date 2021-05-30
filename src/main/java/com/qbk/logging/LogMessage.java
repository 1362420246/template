package com.qbk.logging;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 收集日志的实体类
 */
@Data
public class LogMessage {

    /**
     * 字段分隔符
     */
    private static final char DEFAULT_LOG_FIELD_SEPARATOR_1 = '\u0001';

    /**
     * 字段域内KV分隔符
     */
    public static final String DEFAULT_LOG_FIELD_SEPARATOR_2 = "&";

    /**
     * 占位符
     */
    private static final String DEFAULT_FIELD_PLACEHOLDER = "-";

    /**
     * 请求类型
     */
    private String event;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求URI
     */
    private String requestUri;

    /**
     * 请求IP地址
     */
    private String ip;

    /**
     * traceId
     */
    private String traceId;

    /**
     * 请求头
     */
    private String reqHeaders;

    /**
     * 请求消息体
     */
    private String reqBody;

    /**
     * 响应码
     */
    private int httpStatus;

    /**
     * 响应头
     */
    private String respHeaders;

    /**
     * 响应体
     */
    private String respBody;

    /**
     * 请求耗时
     */
    private long duration;

    public String buildMessage() {
        String[] strings = new String[]{
                event,
                method,
                requestUri,
                StrUtil.emptyToDefault(ip, DEFAULT_FIELD_PLACEHOLDER),
                StrUtil.emptyToDefault(traceId, DEFAULT_FIELD_PLACEHOLDER),
                StrUtil.emptyToDefault(reqHeaders, DEFAULT_FIELD_PLACEHOLDER),
                StrUtil.emptyToDefault(reqBody, DEFAULT_FIELD_PLACEHOLDER),
                String.valueOf(httpStatus),
                StrUtil.emptyToDefault(respHeaders, DEFAULT_FIELD_PLACEHOLDER),
                StrUtil.emptyToDefault(respBody, DEFAULT_FIELD_PLACEHOLDER),
                String.valueOf(duration)
        };
        return DEFAULT_LOG_FIELD_SEPARATOR_1 + String.join(String.valueOf(DEFAULT_LOG_FIELD_SEPARATOR_1), strings);
    }
}
