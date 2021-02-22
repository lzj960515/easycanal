package com.github.lzj960515.core.support;

import com.alibaba.otter.canal.client.CanalConnector;
import com.github.lzj960515.core.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * canal message
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class CanalMessage {

    private CanalConnector canalConnector;

    private EventType eventType;

    private Map<String, String> allColumn;
    /**
     * 只有更新了的列，包含主键
     */
    private Map<String, String> onlyUpdateColumn;

}
