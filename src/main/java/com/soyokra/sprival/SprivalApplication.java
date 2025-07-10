package com.soyokra.sprival;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableCaching
@MapperScan("com.soyokra.sprival.dao.*.mapper")
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
})
public class SprivalApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SprivalApplication.class);
        ApplicationContext applicationContext = application.run(args);
    }
}
