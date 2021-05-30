package com.qbk.common;

import com.qbk.i18n.MessageSourceService;
import com.qbk.utils.SpringContextUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * 统一返回值
 */
@Data
public class RestResponse<T> implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4118386780685379201L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 状态描述
     */
    private String message;

    /**
     * 响应消息体(泛型)
     */
    private T data = null;

    /**
     * 响应消息体(泛型)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Trace trace;

    /**
     * 构造方法
     */
    public RestResponse() {
    }

    /**
     * 构造方法
     *
     * @param code 状态码
     * @param args 错误参数
     */
    public RestResponse(int code, Object... args) {
        this.code = code;
        if (!ArrayUtils.isEmpty(args)) {
            this.message = SpringContextUtils.getBean(MessageSourceService.class).getMessage(code);
        }
    }

    /**
     * 构造方法
     *
     * @param code 状态码
     * @param msg  状态描述
     */
    public RestResponse(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    /**
     * 构造方法
     *
     * @param code   状态码
     * @param message    状态描述
     * @param data 响应消息体(泛型)
     */
    public RestResponse(int code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 静态构造方法，处理成功
     */
    public static <T> RestResponse<T> success() {
        return new RestResponse<>(CodeConstant.SUCCESS);
    }

    /**
     * 静态构造方法，处理成功
     *
     * @param message 前端提示信息
     */
    public static <T> RestResponse<T> success(String message) {
        return new RestResponse<>(CodeConstant.SUCCESS, message);
    }

    /**
     * 静态构造方法，处理成功
     *
     * @param body 响应消息体(泛型)
     */
    public static <T> RestResponse<T> success(T body) {
        RestResponse<T> restResponse = new RestResponse<>(CodeConstant.SUCCESS);
        restResponse.setData(body);
        return restResponse;
    }

    /**
     * 静态构造方法，处理成功
     *
     * @param body 响应消息体(泛型)
     */
    public static <T> RestResponse<T> successBody(T body) {
        RestResponse<T> restResponse = new RestResponse<>(CodeConstant.SUCCESS);
        restResponse.setData(body);
        return restResponse;
    }

    /**
     * 判断response是正确的
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return Objects.equals(this.code, CodeConstant.SUCCESS);
    }

    /**
     * 判断response是错误的
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isFailure() {
        return !isSuccess();
    }

    @Data
    public static class Trace {

        /**
         * 二级错误状态描述
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String subMsg;

        /**
         * 异常类型
         */
        private String errType;

        /**
         * 异常堆栈信息
         */
        private String errTrace;

        /**
         * 调用跟踪栈元素
         */
        private StackTraceElement stackTraceElements;
    }

    public static <T> RestResponse<T> create(int errorCode, String msg, T data) {
        RestResponse<T> restResponse = new RestResponse<>();
        restResponse.setCode(errorCode);
        restResponse.setData(data);
        restResponse.setMessage(msg);
        return restResponse ;
    }
}
