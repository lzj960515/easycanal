package com.github.lzj960515.core.context;

import com.github.lzj960515.core.annotation.OnCanalMessage;
import com.github.lzj960515.core.support.MethodHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * onCanalMessage 注解扫描器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public class OnCanalMessageBeanScanner{

    public static void scan(ApplicationContext applicationContext, EasyCanalContext easyCanalContext){
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Map<Method, OnCanalMessage> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<OnCanalMessage>) method -> AnnotatedElementUtils.findMergedAnnotation(method, OnCanalMessage.class));
            annotatedMethods.forEach((method, onCanalMessage) -> {
                if(onCanalMessage == null){
                    return;
                }
                easyCanalContext.registerMethodHandler(new MethodHandler(onCanalMessage.schemaName(), onCanalMessage.tableName(), onCanalMessage.id(), onCanalMessage.eventType(), bean, method));
            });
        }
    }

}
