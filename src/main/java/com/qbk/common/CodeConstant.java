package com.qbk.common;

/**
 * 统一返回值
 */
@SuppressWarnings("unused")
public interface CodeConstant {

    /** 成功 */
    int SUCCESS = 200;

    /** 参数校验无效异常:MethodArgumentNotValidException */
    int ERR_METHOD_ARGUMENT_NOT_VALID = 1;

    /** 绑定异常:BindException */
    int ERR_BIND = 2;

    /** 空指针异常:NullPointerException */
    int ERR_NULL_POINTER = 3;

    /** 非法参数异常:IllegalArgumentException */
    int ERR_ILLEGAL_ARGUMENT = 4;

    /** 非法状态异常:IllegalStateException */
    int ERR_ILLEGAL_STATE = 5;

    /** 计算异常:ArithmeticException */
    int ERR_ARITHMETIC = 6;

    /** 类型转换异常:ClassCastException */
    int ERR_CLASS_CAST = 7;

    /** 集合负数异常:NegativeArraySizeException */
    int ERR_NEGATIVE_ARRAY_SIZE = 8;

    /** 集合超出范围异常:ArrayIndexOutOfBoundsException */
    int ERR_ARRAY_INDEX_OUT_OF_BOUNDS = 9;

    /** 方法未找到异常:NoSuchMethodException */
    int ERR_NO_SUCH_METHOD = 10;

    /** SQL异常:SQLException */
    int ERR_SQL = 11;

    /** 读写异常:IOException */
    int ERR_IO = 12;

    /** 不支持的编码异常:UnsupportedEncodingException */
    int ERR_UNSUPPORTED_ENCODING = 13;

    /** 邮件发送异常:org.springframework.mail.MailException */
    int ERR_MAIL_SEND = 14;

    /** Excel生成异常:BusinessException */
    int ERR_EXCEL_CREATE = 15;

    /** 找不到处理器异常:NoHandlerFoundException */
    int ERR_NO_HANDLER_FOUND = 21;

    /** 请求方法不支持异常:HttpRequestMethodNotSupportedException */
    int ERR_HTTP_REQUEST_METHOD_NOT_SUPPORTED = 22;

    /** 请求类型不支持异常:HttpMediaTypeNotSupportedException */
    int ERR_HTTP_MEDIA_TYPE_NOT_SUPPORTED = 23;

    /** 缺失路径变量异常:MissingPathVariableException */
    int ERR_MISSING_PATH_VARIABLE = 24;

    /** 类型不匹配异常:TypeMismatchException */
    int ERR_TYPE_MISMATCH = 25;

    /** HttpMessage不可读异常:HttpMessageNotReadableException */
    int ERR_HTTP_MESSAGE_NOT_READABLE = 26;

    /** HttpMessage不可写异常:HttpMessageNotWritableException */
    int ERR_HTTP_MESSAGE_NOT_WRITABLE = 27;

    /** 请求类型不接受异常:HttpMediaTypeNotAcceptableException */
    int ERR_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE = 28;

    /** Servlet请求绑定异常:ServletRequestBindingException */
    int ERR_REQUEST_BINDING = 29;

    /** 缺失Servlet请求异常:MissingServletRequestPartException */
    int ERR_MISSING_SERVLET_REQUEST_PART = 30;

    /** 异步请求超时异常:AsyncRequestTimeoutException */
    int ERR_ASYNC_REQUEST_TIMEOUT = 31;



    /** 服务调用客户端异常:ClientException */
    int ERR_NETFLIX_CLIENT = 40;

    /** 服务调用重试异常:RetryableException */
    int ERR_NETFLIX_RETRYABLE = 41;

    /** 服务调用重试异常:RestClientResponseException */
    int ERR_REST_CLIENT_RESPONSE = 42;

    /** 网关错误:com.netflix.zuul.exception.ZuulException */
    int ERR_NETFLIX_ZUUL = 43;

    /** 未知错误 */
    int ERR_UNKNOWN = 99;

    /** 正常 */
    int NORMAL=0;

    /** 已分配 */
    int IS_DISTRIBUTION=0;

    /** 未分配 */
    int NO_DISTRIBUTION=1;

    /** 开始 */
    int IS_START=1;

    /** 结束 */
    int IS_OVER=2;

    /** 删除 */
    int DELETE=-1;

    /** 开始 */
    int ART_DELETE=-1;

    /** 跳转方式 */
    String CHECK_RESULT_ACTION="check_result_action";

    /** nfc服务IP */
    String NFC_SERVER_IP="nfc_server_ip";

    /** nfc服务端口 */
    String NFC_SERVER_PORT="nfc_server_port";

    /** 可信度阈值 */
    String FACE_THRESHOLD_VALUE="face_threshold_value";

    /** 登录失败提示信息 */
    String LOGIN_FAIL_INFO="未找到身份证号码为【%s】的民警信息。可能原因为：移动警务中民警的身份证号码不正确。";

}
