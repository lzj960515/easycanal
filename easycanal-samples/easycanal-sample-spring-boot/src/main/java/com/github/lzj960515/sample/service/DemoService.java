package com.github.lzj960515.sample.service;

import com.alibaba.otter.canal.client.CanalConnector;
import com.github.lzj960515.core.annotation.OnCanalMessage;
import com.github.lzj960515.core.enums.EventType;
import com.github.lzj960515.core.support.CanalMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * demo
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class DemoService {

    @OnCanalMessage(schemaName = "test", tableName = "fruit", id = "id", eventType = EventType.INSERT)
    public void onInsertMessage(CanalMessage canalMessage){
        System.out.println("监听到表test.fruit插入");
        Map<String, String> allColumn = canalMessage.getAllColumn();
        Map<String, String> onlyUpdateColumn = canalMessage.getOnlyUpdateColumn();
        System.out.println("所有列：" + allColumn);
        System.out.println("更新了的列" + onlyUpdateColumn);
    }

    @OnCanalMessage(schemaName = "test", tableName = "fruit", id = "id", eventType = EventType.UPDATE)
    public void onUpdateMessage(CanalMessage canalMessage){
        System.out.println("监听到表test.fruit更新");
        Map<String, String> allColumn = canalMessage.getAllColumn();
        Map<String, String> onlyUpdateColumn = canalMessage.getOnlyUpdateColumn();
        System.out.println("所有列：" + allColumn);
        System.out.println("更新了的列" + onlyUpdateColumn);
    }

    @OnCanalMessage(schemaName = "test", tableName = "fruit", id = "id", eventType = EventType.DELETE)
    public void onDeleteMessage(CanalMessage canalMessage){
        System.out.println("监听到表test.fruit删除");
        Map<String, String> allColumn = canalMessage.getAllColumn();
        Map<String, String> onlyUpdateColumn = canalMessage.getOnlyUpdateColumn();
        System.out.println("所有列：" + allColumn);
        System.out.println("更新了的列" + onlyUpdateColumn);
    }
}
