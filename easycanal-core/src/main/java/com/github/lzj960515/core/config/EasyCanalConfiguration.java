package com.github.lzj960515.core.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.github.lzj960515.core.context.EasyCanalContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;

/**
 * 自动配置类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(CanalProperties.class)
public class EasyCanalConfiguration {

    @Autowired
    private CanalProperties canalProperties;

    @Bean
    public EasyCanalContext easyCanalContext(){
        return new EasyCanalContext();
    }

    @Bean
    public CanalConnector canalConnector(){
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalProperties.getHost(), canalProperties.getPort()),
                canalProperties.getDestination(),
                canalProperties.getUsername(),
                canalProperties.getPassword());
        connector.connect();
        if(StringUtils.hasText(canalProperties.getFilter())){
            connector.subscribe(canalProperties.getFilter());
        }
        else {
            connector.subscribe();
        }
        // 回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拉取数据
        connector.rollback();
        return connector;
    }
}
