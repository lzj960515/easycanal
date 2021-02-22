# easycanal
简化canal的使用方式，集成Spring, 只需加上配置与相应的注解，即可像MQ一样使用canal

## 使用方式

- starter方式

  - 引入`local-schema-spring-boot-starter`依赖

    ```xml
     <dependency>
            <groupId>com.github.lzj960515</groupId>
            <artifactId>easycanal-spring-boot-starter</artifactId>
            <version>1.0.0</version>
     </dependency>
    ```
  - 编写配置
     ```yaml
      canal:
        server:
          host: 127.0.0.1
          port: 11111
          destination: example
          username: canal
          password: canal 
     ```
  - 在使用的方法上加上`@OnCanalMessage`注解
    ```java
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
        }
    ```
    > schemaName: 数据库名                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
    tableName: 表名                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
    id: 表的主键                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
    eventType: 事件类型                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    