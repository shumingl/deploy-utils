package com.iwill.deploy.common.utils.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

@SuppressWarnings("unchecked")
public class ContextUtil implements ApplicationContextAware {

    private static ApplicationContext context = null;
    private static ConfigurableListableBeanFactory factory;

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ContextUtil.context = context;
        factory = ((ConfigurableApplicationContext) context).getBeanFactory();
    }

    public static <T> T getBean(String beanId, Class<T> clazz) throws BeansException {
        return context.getBean(beanId, clazz);
    }

    public static <T> T getBean(Class<T> clazz) throws BeansException {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String beanId) throws BeansException {
        return (T) context.getBean(beanId);
    }

    public static void registerSingleton(String beanid, Object bean) {
        factory.registerSingleton(beanid, bean);
    }

    public static boolean containsBean(String beanid) {
        return factory.containsBean(beanid);
    }


}
