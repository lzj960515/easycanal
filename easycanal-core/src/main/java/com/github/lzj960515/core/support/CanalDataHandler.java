package com.github.lzj960515.core.support;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.github.lzj960515.core.context.EasyCanalContext;
import com.github.lzj960515.core.enums.EventType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Canal 数据处理
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
public class CanalDataHandler {

    private static final int MAX_SIZE = 1024;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "easy-canal-data-thread");
        }
    });

    private final CanalConnector connector;

    private final EasyCanalContext easyCanalContext;

    public CanalDataHandler(CanalConnector canalConnector, EasyCanalContext easyCanalContext){
        this.connector = canalConnector;
        this.easyCanalContext = easyCanalContext;
    }

    public void start(){
        executor.execute(this::process);
    }

    public void process(){
        for(;;){
            try{
                Message message = connector.getWithoutAck(MAX_SIZE, 2L, TimeUnit.SECONDS);
                long id = message.getId();
                List<CanalEntry.Entry> entries = message.getEntries();
                if(id != -1 && entries.size() > 0){
                    handleMessage(entries);
                }
                connector.ack(id);
            }catch (Throwable e){
                log.error("处理canal消息发生异常：", e);
            }
        }
    }

    private void handleMessage(List<CanalEntry.Entry> entries){
        entries.forEach(entry -> {
            if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                // 数据库库名
                String schemaName = entry.getHeader().getSchemaName();
                String tableName = entry.getHeader().getTableName();
                CanalEntry.RowChange rowChange = null;
                try {
                    rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                } catch (Exception e) {
                    throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
                }
                CanalEntry.EventType eventType = rowChange.getEventType();
                rowChange.getRowDatasList().forEach(rowData -> {
                    List<CanalEntry.Column> columnList;
                    if(eventType == CanalEntry.EventType.DELETE){
                        columnList = rowData.getBeforeColumnsList();
                    }else {
                        columnList = rowData.getAfterColumnsList();
                    }
                    MethodHandler methodHandler = easyCanalContext.getMethodHandler(schemaName, tableName, determineEventType(eventType));
                    if(methodHandler != null){
                        methodHandler.execute(connector, columnList);
                    }
                });
            }
        });
    }

    private EventType determineEventType(CanalEntry.EventType eventType){
        if(eventType == CanalEntry.EventType.INSERT){
            return EventType.INSERT;
        }
        if(eventType == CanalEntry.EventType.UPDATE){
            return EventType.UPDATE;
        }
        if(eventType == CanalEntry.EventType.DELETE){
            return EventType.DELETE;
        }
        return null;
    }
}
