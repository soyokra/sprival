## 客户端组件

- jdbc
- mybatis
- hikari
- mybatis-plus
- p6spy
- micrometer
- dropwizard

## 配置说明

### mybatis-plus

```yml
spring.datasource.dynamic.datasource.primary = master # 默认库

# 主库
spring.datasource.dynamic.datasource.master.username = root
spring.datasource.dynamic.datasource.master.password = workdock
spring.datasource.dynamic.datasource.master.url = jdbc:mysql://localhost:3306/sprival
spring.datasource.dynamic.datasource.master.driver-class-name = com.mysql.jdbc.Driver
spring.datasource.dynamic.datasource.master.type = com.zaxxer.hikari.HikariDataSource #使用Hikaricp

# 从库
spring.datasource.dynamic.datasource.slave.username = root
spring.datasource.dynamic.datasource.slave.password = workdock
spring.datasource.dynamic.datasource.slave.url = jdbc:mysql://localhost:3306/sprival
spring.datasource.dynamic.datasource.slave.driver-class-name = com.mysql.jdbc.Driver
spring.datasource.dynamic.datasource.slave.type = com.zaxxer.hikari.HikariDataSource #使用Hikaricp

# hikari
# spring.datasource.dynamic.datasource.master.hikari.xxx
# spring.datasource.dynamic.datasource.hikari.xxx
```

> 连接池可以全局配置，也可以每个连接库单独配置

### hikari

```yml
## hikari全局配置
spring.datasource.dynamic.hikari.is-auto-commit =  true
spring.datasource.dynamic.hikari.max_lifetime = 30000
spring.datasource.dynamic.hikari.min_idle = 10
spring.datasource.dynamic.hikari.max_pool_size = 1000
spring.datasource.dynamic.hikari.idle_timeout = 10000
spring.datasource.dynamic.hikari.connection_timeout = 10000
spring.datasource.dynamic.hikari.validation_timeout = 1000
spring.datasource.dynamic.hikari.connection_init_sql = set session wait_timeout=28800,interactive_timeout=28800;
```

- autoCommit: 事务自动提交，默认值TRUE
- connectionTimeout: 从连接池拿连接的超时时长, 最小值250毫秒，默认30秒
- idleTimeout：空闲连接超时时间，超过最小空闲连接数的连接空闲保持的存活的时长，单位毫秒，最小值10秒， 默认值10分钟
- maxLifetime：最小空闲连接保持存活的最大时长，这个值应该小于数据库允许客户端连接存活的最大时长，单位毫秒，最小值30秒，默认值30分钟
- connectionTestQuery：用于驱动不支持JDBC4 连接检查Connection.isValid()接口，用于心跳检查
- minimumIdle：最小空闲连接，默认值和maximumPoolSize。建议和maximumPoolSize一样，作为固定连接池使用
- maximumPoolSize：最大连接数，应用请求超过最大连接数的时候，会等待connectionTimeout再抛出异常

> 最小空闲连接：假设minimumIdle=10，maximumPoolSize=15，应用一直占用10个连接，那么hikari会参试创建10个空闲连接，但是受到最大连接数限制，只会创建5个空闲连接
> 假设应用释放了这个10个连接，由于最小空闲连接数是10个，hikari会关掉5个空闲连接

参考文档：[https://github.com/brettwooldridge/HikariCP/tree/HikariCP-3.4.5](https://github.com/brettwooldridge/HikariCP/tree/HikariCP-3.4.5)

### p6spy

### micrometer监控hikari

由于mybatis-plus默认的HikariDataSourceCreator不支持设置metricRegistry，因此需要改造一下

结合DynamicDataSourceCreatorAutoConfiguration的dataSourceCreator方法和DefaultDataSourceCreator的
createDataSource可知，可以注册多个DataSourceCreator类型的Bean，最终只会使用一个，由于我们使用的是hikari，因此
需要在hikariDataSourceCreator之前注册一个增强版的hikariDataSourcePlusCreator

```java
@Configuration
public class DynamicDataSourceCreatorAutoConfiguration {

    public static final int JNDI_ORDER = 1000;
    public static final int DRUID_ORDER = 2000;
    public static final int HIKARI_ORDER = 3000;
    public static final int BEECP_ORDER = 4000;
    public static final int DBCP2_ORDER = 5000;
    public static final int DEFAULT_ORDER = 6000;

    @Primary
    @Bean
    @ConditionalOnMissingBean
    public DefaultDataSourceCreator dataSourceCreator(List<DataSourceCreator> dataSourceCreators) {
        DefaultDataSourceCreator defaultDataSourceCreator = new DefaultDataSourceCreator();
        defaultDataSourceCreator.setCreators(dataSourceCreators);
        return defaultDataSourceCreator;
    }

    @ConditionalOnClass(HikariDataSource.class)
    @Configuration
    static class HikariDataSourceCreatorConfiguration {
        @Bean
        @Order(HIKARI_ORDER)
        public HikariDataSourceCreator hikariDataSourceCreator() {
            return new HikariDataSourceCreator();
        }
    }
}

@Slf4j
@Setter
public class DefaultDataSourceCreator {

    private List<DataSourceCreator> creators;

    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        DataSourceCreator dataSourceCreator = null;
        for (DataSourceCreator creator : this.creators) {
            if (creator.support(dataSourceProperty)) {
                dataSourceCreator = creator;
                break;
            }
        }
        if (dataSourceCreator == null) {
            throw new IllegalStateException("creator must not be null,please check the DataSourceCreator");
        }
        return dataSourceCreator.createDataSource(dataSourceProperty);
    }

}
```

注册hikariDataSourcePlusCreator

```java
public class HikariDataSourcePlusCreator extends HikariDataSourceCreator {

    private final MeterRegistry meterRegistry;

    public HikariDataSourcePlusCreator(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public DataSource doCreateDataSource(DataSourceProperty dataSourceProperty) {
        HikariDataSource dataSource = (HikariDataSource) super.doCreateDataSource(dataSourceProperty);
        dataSource.setMetricRegistry(meterRegistry);
        return dataSource;
    }
}

@Configuration
public class HikariDataSourcePlusCreatorAutoConfiguration {
    @ConditionalOnClass(HikariDataSource.class)
    @Configuration
    static class HikariDataSourcePlusCreatorConfiguration {
        @Bean
        @Order(DynamicDataSourceCreatorAutoConfiguration.HIKARI_ORDER-1)
        public HikariDataSourcePlusCreator hikariDataSourcePlusCreator(MeterRegistry meterRegistry) {
            return new HikariDataSourcePlusCreator(meterRegistry);
        }
    }
}
```
