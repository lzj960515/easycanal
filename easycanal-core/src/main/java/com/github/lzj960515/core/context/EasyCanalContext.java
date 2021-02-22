package com.github.lzj960515.core.context;

import com.alibaba.otter.canal.client.CanalConnector;
import com.github.lzj960515.core.enums.EventType;
import com.github.lzj960515.core.support.CanalDataHandler;
import com.github.lzj960515.core.support.MethodHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 启动器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
public class EasyCanalContext implements ApplicationListener<ContextRefreshedEvent> {

    private static final Map<String, Map<EventType, MethodHandler>> methodHandlerRepository = new ConcurrentHashMap<>(32);

    @Autowired
    private CanalConnector canalConnector;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        OnCanalMessageBeanScanner.scan(contextRefreshedEvent.getApplicationContext(), this);
        new CanalDataHandler(canalConnector, this).start();
        log.info("Easy Canal loaded!");
    }

    public void registerMethodHandler(MethodHandler methodHandler) {
        String key = getKey(methodHandler.getSchemaName(), methodHandler.getTableName());
        if (methodHandlerRepository.containsKey(key)) {
            Map<EventType, MethodHandler> methodHandlerMap = methodHandlerRepository.get(key);
            if (EventType.ALL == methodHandler.getEventType()) {
                Set<EventType> eventTypes = methodHandlerMap.keySet();
                throw new RuntimeException("存在有 @onCanalMessage -> eventType为 " + eventTypes + " 修饰的方法 schemaName: [" + methodHandler.getSchemaName() + "]," +
                        " tableName: [" + methodHandler.getTableName() + "], 不可再增加新的eventType: [" + methodHandler.getEventType() + "]");
            }
            if (methodHandlerMap.containsKey(EventType.ALL)) {
                throw new RuntimeException("存在有 @onCanalMessage -> eventType为ALL 修饰的方法 schemaName: [" + methodHandler.getSchemaName() + "]," +
                        " tableName: [" + methodHandler.getTableName() + "], 不可再增加新的eventType: [" + methodHandler.getEventType() + "]");
            }
            if (methodHandlerMap.containsKey(methodHandler.getEventType())) {
                throw new RuntimeException("存在相同的 @onCanalMessage 修饰的方法 schemaName: [" + methodHandler.getSchemaName() + "]," +
                        " tableName: [" + methodHandler.getTableName() + "], eventType: [" + methodHandler.getEventType() + "]");
            }
        } else {
            methodHandlerRepository.put(key, new HashMap<>(4));
        }
        Map<EventType, MethodHandler> methodHandlerMap = methodHandlerRepository.get(key);
        methodHandlerMap.put(methodHandler.getEventType(), methodHandler);
    }

    public MethodHandler getMethodHandler(String schemaName, String tableName, EventType eventType) {
        String key = getKey(schemaName, tableName);
        if (!methodHandlerRepository.containsKey(key)) {
            return null;
        }
        Map<EventType, MethodHandler> methodHandlerMap = methodHandlerRepository.get(key);
        if (methodHandlerMap.containsKey(EventType.ALL)) {
            return methodHandlerMap.get(EventType.ALL);
        }
        if (methodHandlerMap.containsKey(eventType)) {
            return methodHandlerMap.get(eventType);
        }
        return null;
    }

    private String getKey(String schemaName, String tableName) {
        return schemaName + "." + tableName;
    }


}
