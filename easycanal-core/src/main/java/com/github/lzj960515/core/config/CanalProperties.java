package com.github.lzj960515.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * canal配置信息
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "canal.server")
public class CanalProperties {
    /**
     * canal server address
     */
    private String host;
    /**
     * canal server address
     */
    private int port;
    /**
     * canal 实例的目标队列
     */
    private String destination;

    private String username;

    private String password;
    /**
     * canal server 监听表的过滤表达式
     * 不配则默认为服务端配置
     */
    private String filter;
}
