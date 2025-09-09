package com.soyokra.sprival;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Sprival应用主测试类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@SpringBootTest(classes = SprivalApplication.class)
@ActiveProfiles("simple")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
class SprivalApplicationTests {

    @Test
    void contextLoads() {
        // 验证Spring上下文能够正常加载
    }

    @Test
    void applicationStarts() {
        // 验证应用能够正常启动
    }
}
