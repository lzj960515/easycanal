package com.github.lzj960515.core.annotation;

import com.github.lzj960515.core.enums.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监听canal消息
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OnCanalMessage {

    String schemaName();

    String tableName();
    /**
     * 该表的主键
     */
    String id();

    EventType eventType();
}
