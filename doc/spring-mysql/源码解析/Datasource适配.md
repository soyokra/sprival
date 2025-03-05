## 前言

## spring集成

### 调用链路
![图片替代文本](DataSource.drawio.png)

### 详细说明

#### DynamicDataSourceAutoConfiguration
这个类注册了两个相关bean
DynamicRoutingDataSource注册为DataSource类型的bean，其他组件从通过spring自动注入DataSource的时候，实际上用的就是DynamicRoutingDataSource
YmlDynamicDataSourceProvider注册为DynamicDataSourceProvider类型的bean，DynamicRoutingDataSource获取真正数据源，比如HikariDataSource，使用到的服务bean

```java
@Slf4j
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureBefore(value = DataSourceAutoConfiguration.class, name = "com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure")
@Import(value = {DruidDynamicDataSourceConfiguration.class, DynamicDataSourceCreatorAutoConfiguration.class})
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDataSourceAutoConfiguration implements InitializingBean {
    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        //省略...
        return dataSource;
    }

    @Bean
    public DynamicDataSourceProvider ymlDynamicDataSourceProvider() {
        return new YmlDynamicDataSourceProvider(properties.getDatasource());
    }
}
```


#### DynamicDataSourceCreatorAutoConfiguration
这个类注册了两个相关bean
HikariDataSourceCreator注册为HikariDataSourceCreator类型的bean，HikariDataSourceCreator实现了DataSourceCreator接口
DefaultDataSourceCreator注册为DefaultDataSourceCreator类型的bean，参数就是DataSourceCreator这个接口类型，实际注入的就是HikariDataSourceCreator
```java
    @ConditionalOnClass(HikariDataSource.class)
    @Configuration
    static class HikariDataSourceCreatorConfiguration {
        @Bean
        @Order(HIKARI_ORDER)
        public HikariDataSourceCreator hikariDataSourceCreator() {
            return new HikariDataSourceCreator();
        }
    }
    
    @Primary
    @Bean
    @ConditionalOnMissingBean
    public DefaultDataSourceCreator dataSourceCreator(List<DataSourceCreator> dataSourceCreators) {
        DefaultDataSourceCreator defaultDataSourceCreator = new DefaultDataSourceCreator();
        defaultDataSourceCreator.setCreators(dataSourceCreators);
        return defaultDataSourceCreator;
    }
```

#### DynamicRoutingDataSource

### 总结
