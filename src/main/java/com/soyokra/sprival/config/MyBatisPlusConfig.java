package com.soyokra.sprival.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@EnableTransactionManagement
@MapperScan(basePackages = "com.soyokra.sprival.dao.*.mapper", nameGenerator = MyBatisPlusConfig.SpringBeanNameGenerator.class)
@Configuration
public class MyBatisPlusConfig {
    public static class SpringBeanNameGenerator extends AnnotationBeanNameGenerator {
        @Override
        protected String buildDefaultBeanName(BeanDefinition definition) {
            return definition.getBeanClassName();
        }
    }
}
