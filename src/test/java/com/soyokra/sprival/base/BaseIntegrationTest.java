package com.soyokra.sprival.base;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 集成测试基类
 * 
 * <p>用途：所有集成测试必须继承此类</p>
 * 
 * <p>功能：</p>
 * <ul>
 *   <li>加载完整的Spring Boot上下文</li>
 *   <li>使用本地环境配置（application.properties）</li>
 *   <li>支持真实的数据库和外部服务连接</li>
 *   <li>数据默认不回滚（适用于本地开发测试环境）</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * @DisplayName("Order Repository集成测试")
 * class OrderRepositoryIntegrationTest extends BaseIntegrationTest {
 *     @Autowired
 *     private OrderTblProvider orderProvider;
 *     
 *     @Test
 *     @DisplayName("测试保存订单")
 *     void testSave() {
 *         OrderTbl order = OrderTblFixture.create();
 *         boolean result = orderProvider.save(order);
 *         assertThat(result).isTrue();
 *     }
 * }
 * }</pre>
 * 
 * <p>注意：</p>
 * <ul>
 *   <li>默认不使用事务回滚，测试数据会保留在数据库中</li>
 *   <li>如需回滚，请在测试类或方法上添加 @Transactional 和 @Rollback 注解</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@SpringBootTest
@ActiveProfiles("dev")
public abstract class BaseIntegrationTest {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * 测试初始化方法
     * 
     * <p>子类可以重写此方法来添加自定义的初始化逻辑</p>
     */
    @BeforeEach
    public void setUp() {
        log.debug("初始化集成测试: {}", getClass().getSimpleName());
    }
    
    /**
     * 打印测试日志
     * 
     * @param message 日志消息
     * @param args 日志参数
     */
    protected void logTest(String message, Object... args) {
        log.info(message, args);
    }
}

