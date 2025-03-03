package com.soyokra.sprival;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.soyokra.sprival.dao.*.mapper")
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
public class SprivalApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SprivalApplication.class);
        ApplicationContext applicationContext = application.run(args);
    }
}
