package com.github.lzj960515.core.annotation;

import com.github.lzj960515.core.context.EasyCanalContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EasyCanalContext.class)
public @interface EnableEasyCanal {
}
