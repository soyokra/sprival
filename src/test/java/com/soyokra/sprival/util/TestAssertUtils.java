package com.soyokra.sprival.util;

import com.soyokra.sprival.app.util.ResponseUtils;
import org.assertj.core.api.Assertions;

/**
 * 测试断言工具类
 * 
 * <p>提供针对业务对象的断言方法</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>ResponseUtils响应断言</li>
 *   <li>成功/失败场景断言</li>
 *   <li>数据验证断言</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * // 断言成功响应
 * TestAssertUtils.assertSuccess(response);
 * 
 * // 断言成功响应且包含数据
 * TestAssertUtils.assertSuccessWithData(response);
 * 
 * // 断言错误响应
 * TestAssertUtils.assertError(response, 1001);
 * }</pre>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
public final class TestAssertUtils {
    
    private TestAssertUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
    
    /**
     * 断言成功响应
     * 
     * @param response 响应对象
     */
    public static void assertSuccess(ResponseUtils<?> response) {
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode())
                .as("响应码应为0（成功）")
                .isEqualTo(0);
        Assertions.assertThat(response.getMessage())
                .as("响应消息不应为空")
                .isNotNull();
    }
    
    /**
     * 断言成功响应且包含数据
     * 
     * @param response 响应对象
     */
    public static void assertSuccessWithData(ResponseUtils<?> response) {
        assertSuccess(response);
        Assertions.assertThat(response.getData())
                .as("响应数据不应为null")
                .isNotNull();
    }
    
    /**
     * 断言成功响应且数据匹配
     * 
     * @param response 响应对象
     * @param expectedData 期望的数据
     * @param <T> 数据类型
     */
    public static <T> void assertSuccessWithData(ResponseUtils<T> response, T expectedData) {
        assertSuccess(response);
        Assertions.assertThat(response.getData())
                .as("响应数据应与期望数据一致")
                .isEqualTo(expectedData);
    }
    
    /**
     * 断言错误响应
     * 
     * @param response 响应对象
     * @param expectedCode 期望的错误码
     */
    public static void assertError(ResponseUtils<?> response, int expectedCode) {
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode())
                .as("错误码应为 " + expectedCode)
                .isEqualTo(expectedCode);
    }
    
    /**
     * 断言错误响应（包含错误消息）
     * 
     * @param response 响应对象
     * @param expectedCode 期望的错误码
     * @param expectedMessage 期望的错误消息（部分匹配）
     */
    public static void assertError(ResponseUtils<?> response, int expectedCode, String expectedMessage) {
        assertError(response, expectedCode);
        Assertions.assertThat(response.getMessage())
                .as("错误消息应包含: " + expectedMessage)
                .contains(expectedMessage);
    }
    
    /**
     * 断言响应码
     * 
     * @param response 响应对象
     * @param expectedCode 期望的响应码
     */
    public static void assertResponseCode(ResponseUtils<?> response, int expectedCode) {
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode())
                .as("响应码应为 " + expectedCode)
                .isEqualTo(expectedCode);
    }
}

