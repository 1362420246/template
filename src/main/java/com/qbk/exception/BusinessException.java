package com.qbk.exception;

import com.qbk.common.ResultStatus;
import com.qbk.i18n.MessageSourceService;
import com.qbk.utils.SpringContextUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -4435979898433185113L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误结构
     */
    @Setter
    @Accessors(chain = true)
    private Object body;

    /**
     * 是否报警
     */
    private boolean alarm;

    /**
     * @param code 错误码，自动从MessageSourceService获取message
     */
    public BusinessException(int code) {
        super(SpringContextUtils.getBean(MessageSourceService.class).getMessage(code));
        this.code = code;
    }

    /**
     * @param code 错误码，自动从MessageSourceService获取message
     * @param args message配置文件对应的{0}参数
     */
    public BusinessException(int code, Object[] args) {
        super(SpringContextUtils.getBean(MessageSourceService.class).getMessage(code, args));
        this.code = code;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultStatus resultStatus) {
        super(resultStatus.getErrorMsg());
        this.code = resultStatus.getErrorCode();
    }



    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException alarm() {
        this.alarm = true;
        return this;
    }

    public int getCode() {
        return code;
    }

    public boolean isAlarm() {
        return alarm;
    }

    /**
     * 通用失败异常返回
     */
    public static BusinessException getFailException( String message) {
        return new BusinessException(ResultStatus.FAIL.getErrorCode(),message);
    }
}
