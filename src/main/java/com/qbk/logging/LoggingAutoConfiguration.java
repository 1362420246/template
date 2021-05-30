package com.qbk.logging;

import com.qbk.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Configuration
public class LoggingAutoConfiguration {

    @Autowired
    private ApplicationConfig.LogConfig logConfig;

    @Bean
    public FilterRegistrationBean<?> requestLoggingFilter() {
        RequestLoggingFilter filter = new RequestLoggingFilter(logConfig);
        FilterRegistrationBean<?> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
}
