package com.qbk.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@SuppressWarnings({"WeakerAccess", "unused"})
@Component
public class SpringContextUtils implements ApplicationContextAware {

    /**
     * applicationContext
     */
    private static ApplicationContext applicationContext = null;

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     *
     * @param applicationContext 上下文
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtils.applicationContext == null) {
            SpringContextUtils.applicationContext = applicationContext;
        }
    }

    /**
     * 获取applicationContext
     *
     * @return applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 根据name获取Bean
     *
     * @param name 名字
     * @return Bean
     */
    public static Object getBean(String name) {
        checkApplicationContext();
        return getApplicationContext().getBean(name);
    }

    /**
     * 根据类获取Bean
     *
     * @param clazz 类型
     * @param <T>   泛型
     * @return Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 根据name和类获取Bean
     *
     * @param name  名字
     * @param clazz 类型
     * @param <T>   泛型
     * @return Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        checkApplicationContext();
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 清除applicationContext静态变量.
     */
    public static void cleanApplicationContext() {
        checkApplicationContext();
        applicationContext = null;
    }


    /**
     * 检测 checkApplicationContext void
     */
    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicationContext未注入,请在applicationContext.xml中定义SpringContextHolder");
        }
    }
}
