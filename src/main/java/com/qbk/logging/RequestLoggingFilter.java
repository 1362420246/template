package com.qbk.logging;

import com.qbk.config.ApplicationConfig;
import cn.hutool.core.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * WEB请求日志数据拼装
 */
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    /**
     * 请求EVENT
     */
    private static final String LOG_EVENT = "REQ";

    /**
     * TraceId分隔符
     */
    private static final String PINT_POINT_SEPARATOR = "^";

    /**
     * 打印日志开关
     */
    private boolean enable;

    /**
     * body最大长度
     */
    private int maxLength;

    private static final String FILTER_ACTUATOR = "/actuator/";
    private static final String FILTER_FAVICON = "/favicon.ico";

    RequestLoggingFilter(ApplicationConfig.LogConfig logConfig) {
        this.enable = logConfig.isRequestEnable();
        this.maxLength = logConfig.getRequestMaxLength();
    }

    /**
     * 是否处理异步请求，false为处理
     * <p>
     * The default return value is "true", which means the filter will not be
     * invoked during subsequent async dispatches. If "false", the filter will
     * be invoked during async dispatches with the same guarantees of being
     * invoked only once during a input within a single thread.
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    /**
     * 核心方法，记录日志
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (!enable || request.getRequestURI().startsWith(FILTER_ACTUATOR) || Objects.equals(request.getRequestURI(), FILTER_FAVICON)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, maxLength);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            log.info(createMessage(requestWrapper, responseWrapper, startTime));
            responseWrapper.copyBodyToResponse();
        }
    }

    /**
     * 拼接日志
     */
    private String createMessage(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long startTime) {
        LogMessage logMsg = new LogMessage();
        logMsg.setEvent(LOG_EVENT + (isAsyncDispatch(request) ? "_ASYNC" : ""));
        logMsg.setMethod(request.getMethod());
        logMsg.setRequestUri(StringUtils.isEmpty(request.getQueryString()) ?
                request.getRequestURI() : request.getRequestURI() + "?" + request.getQueryString());
        logMsg.setIp(request.getRemoteAddr());
        logMsg.setTraceId(assembleApmTraceId(request));
        logMsg.setReqHeaders(assembleReqHeaders(request));
        logMsg.setReqBody(new String(request.getContentAsByteArray(), CharsetUtil.CHARSET_UTF_8).replace("\n", ""));
        logMsg.setHttpStatus(response.getStatus());
        logMsg.setRespHeaders(assembleRespHeaders(response));
        logMsg.setRespBody(new String(response.getContentAsByteArray(), CharsetUtil.CHARSET_UTF_8).replace("\n", ""));
        logMsg.setDuration(System.currentTimeMillis() - startTime);
        return logMsg.buildMessage();
    }

    private String assembleReqHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            sb.append(key).append("=").append(request.getHeader(key))
                    .append(LogMessage.DEFAULT_LOG_FIELD_SEPARATOR_2);
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.substring(0, sb.length() - 1);
    }

    private String assembleRespHeaders(HttpServletResponse response) {
        return response.getHeaderNames().stream()
                .map(
                        key -> key + "=" + response.getHeader(key)
                ).collect(
                        Collectors.joining(LogMessage.DEFAULT_LOG_FIELD_SEPARATOR_2)
                );
    }

    /**
     * 拼装Pinpoint字节码对象内的各项字段（,spanId,parentSpanId）
     * traceId(agentId^agentStartTime^transactionSequence),spanId,parentSpanId
     *
     * @param req 请求对象
     */
    private String assembleApmTraceId(ServletRequest req) {
        while (req instanceof ServletRequestWrapper) {
            req = ((ServletRequestWrapper) req).getRequest();
        }

        if (req instanceof RequestFacade) {
            RequestFacade sr = (RequestFacade) req;
            try {
                Field f = ReflectionUtils.findField(sr.getClass(), "input");
                if (f == null) {
                    return null;
                }
                f.setAccessible(true);
                Object o = ReflectionUtils.getField(f, sr);
                if (o == null) {
                    return null;
                }

                Field fta = ReflectionUtils.findField(o.getClass(),
                        "_$PINPOINT$_com_navercorp_pinpoint_plugin_tomcat_TraceAccessor");
                if (fta == null) {
                    return null;
                }
                fta.setAccessible(true);
                Object ta = ReflectionUtils.getField(fta, o);
                if (ta == null) {
                    return null;
                }

                Field span = ReflectionUtils.findField(ta.getClass(), "span");
                if (span == null) {
                    return null;
                }
                span.setAccessible(true);
                Object spanObj = ReflectionUtils.getField(span, ta);
                if (spanObj == null) {
                    return null;
                }

                Field traceRoot = ReflectionUtils.findField(spanObj.getClass(), "traceRoot");
                if (traceRoot == null) {
                    return null;
                }
                traceRoot.setAccessible(true);
                Object traceRootObj = ReflectionUtils.getField(traceRoot, spanObj);
                if (traceRootObj == null) {
                    return null;
                }

                // traceid【agentId^agentStartTime^transactionSequence】
                Field traceId = ReflectionUtils.findField(traceRootObj.getClass(), "traceId");
                if (traceId == null) {
                    return null;
                }
                traceId.setAccessible(true);
                Object traceIdObj = ReflectionUtils.getField(traceId, traceRootObj);
                if (traceIdObj == null) {
                    return null;
                }

                // agentId
                Class<?> traceIdClass = traceIdObj.getClass();

                Field agentId = ReflectionUtils.findField(traceIdClass, "agentId");
                if (agentId == null) {
                    return null;
                }
                agentId.setAccessible(true);
                Object agentIdVal = ReflectionUtils.getField(agentId, traceIdObj);

                // agentStartTime
                Field agentStartTime = ReflectionUtils.findField(traceIdClass, "agentStartTime");
                if (agentStartTime == null) {
                    return null;
                }
                agentStartTime.setAccessible(true);
                Object agentStartTimeVal = ReflectionUtils.getField(agentStartTime, traceIdObj);
                if (agentStartTimeVal == null) {
                    return null;
                }

                // transactionSequence
                Field transactionSequence = ReflectionUtils.findField(traceIdClass, "transactionSequence");
                if (transactionSequence == null) {
                    return null;
                }
                transactionSequence.setAccessible(true);
                Object transactionSequenceVal = ReflectionUtils.getField(transactionSequence, traceIdObj);
                if (transactionSequenceVal == null) {
                    return null;
                }

                // spanId
                Field spanId = ReflectionUtils.findField(traceIdClass, "spanId");
                if (spanId == null) {
                    return null;
                }
                spanId.setAccessible(true);
                Object spanIdVal = ReflectionUtils.getField(spanId, traceIdObj);
                if (spanIdVal == null) {
                    return null;
                }

                // parentSpanId
                Field parentSpanId = ReflectionUtils.findField(traceIdClass, "parentSpanId");
                if (parentSpanId == null) {
                    return null;
                }
                parentSpanId.setAccessible(true);
                Object parentSpanIdVal = ReflectionUtils.getField(parentSpanId, traceIdObj);
                if (parentSpanIdVal == null) {
                    return null;
                }

                return agentIdVal + PINT_POINT_SEPARATOR + agentStartTimeVal +
                        PINT_POINT_SEPARATOR + transactionSequenceVal +
                        LogMessage.DEFAULT_LOG_FIELD_SEPARATOR_2 + spanIdVal +
                        LogMessage.DEFAULT_LOG_FIELD_SEPARATOR_2 + parentSpanIdVal;

            } catch (Exception e) {
                // nothing
            }
        }

        // 未加载Pinpoint时
        return null;
    }
}
