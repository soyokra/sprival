package com.soyokra.sprival.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单元测试基类
 * 
 * <p>用途：所有单元测试必须继承此类</p>
 * 
 * <p>功能：</p>
 * <ul>
 *   <li>自动初始化Mockito注解（@Mock, @InjectMocks）</li>
 *   <li>统一日志配置</li>
 *   <li>提供通用的测试工具方法</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * @DisplayName("OrderService单元测试")
 * class OrderServiceTest extends BaseUnitTest {
 *     @Mock
 *     private OrderTblProvider orderProvider;
 *     
 *     @InjectMocks
 *     private OrderService orderService;
 *     
 *     @Test
 *     @DisplayName("测试获取订单")
 *     void testGetOrder() {
 *         // 测试代码
 *     }
 * }
 * }</pre>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * 测试初始化方法
     * 
     * <p>子类可以重写此方法来添加自定义的初始化逻辑</p>
     */
    @BeforeEach
    public void setUp() {
        log.debug("初始化单元测试: {}", getClass().getSimpleName());
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

