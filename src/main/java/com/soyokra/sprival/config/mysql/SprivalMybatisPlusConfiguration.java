package com.soyokra.sprival.config.mysql;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 
 * @author Sprival
 * @version 1.0
 */
@Configuration
public class SprivalMybatisPlusConfiguration {

    /**
     * MyBatis-Plus 拦截器配置 包含分页插件和乐观锁插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor =
                new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置单页分页条数限制，默认无限制
        paginationInnerInterceptor.setMaxLimit(500L);
        // 溢出总页数后是否进行处理
        paginationInnerInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }

    /**
     * MyBatis-Plus 全局配置 包含逻辑删除、自动填充等配置
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();

        // 数据库配置
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        // 逻辑删除字段名
        dbConfig.setLogicDeleteField("deleted");
        // 逻辑删除全局值（1表示已删除）
        dbConfig.setLogicDeleteValue("1");
        // 逻辑未删除全局值（0表示未删除）
        dbConfig.setLogicNotDeleteValue("0");
        // 字段验证策略
        dbConfig.setInsertStrategy(com.baomidou.mybatisplus.annotation.FieldStrategy.NOT_NULL);
        dbConfig.setUpdateStrategy(com.baomidou.mybatisplus.annotation.FieldStrategy.NOT_NULL);
        dbConfig.setWhereStrategy(com.baomidou.mybatisplus.annotation.FieldStrategy.NOT_NULL);

        globalConfig.setDbConfig(dbConfig);
        return globalConfig;
    }
}
