package com.github.lzj960515.core.support;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.github.lzj960515.core.enums.EventType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 方法执行器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class MethodHandler {

    private final String schemaName;

    private final String tableName;

    private final String id;

    private final EventType eventType;

    private final Object bean;

    private final Method method;

    public void execute(CanalConnector canalConnector, List<CanalEntry.Column> columnList){
        Map<String, String> allColumn = new LinkedHashMap<>(columnList.size());
        Map<String, String> onlyUpdateColumn = new LinkedHashMap<>(columnList.size());
        columnList.forEach(column -> {
            String name = column.getName();
            String value = column.getValue();
            allColumn.put(name, value);
            boolean updated = column.getUpdated();
            if(updated || id.equals(name)){
                onlyUpdateColumn.put(name, value);
            }
        });
        try {
            method.invoke(bean, new CanalMessage(canalConnector, eventType, allColumn, onlyUpdateColumn));
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("类：{}, 方法：{}，执行失败，原因：", bean.getClass().getCanonicalName(), method.getName(), e);
        }
    }

}
