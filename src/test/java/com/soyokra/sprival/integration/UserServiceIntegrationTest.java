package com.soyokra.sprival.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.soyokra.sprival.client.UserServiceClient;
import com.soyokra.sprival.service.UserService;
import com.soyokra.sprival.util.TestConstants;
import com.soyokra.sprival.util.TestDataBuilder;

/**
 * 用户服务集成测试
 * 
 * @author Sprival Team
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("用户服务集成测试")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserServiceClient userServiceClient;

    @Test
    @DisplayName("用户服务完整流程测试")
    void testUserServiceCompleteFlow() {
        // Given
        UserServiceClient.UserResponse testUser = TestDataBuilder.buildUserResponse(
                TestConstants.TEST_USER_ID, TestConstants.TEST_USERNAME, TestConstants.TEST_EMAIL);
        UserServiceClient.CreateUserRequest createRequest = TestDataBuilder
                .buildCreateUserRequest(TestConstants.TEST_USERNAME, TestConstants.TEST_EMAIL);
        UserServiceClient.UpdateUserRequest updateRequest = TestDataBuilder
                .buildUpdateUserRequest(TestConstants.TEST_USERNAME, TestConstants.TEST_EMAIL);
        List<UserServiceClient.UserResponse> userList = TestDataBuilder.buildUserList(3);

        // Mock 所有方法调用
        when(userServiceClient.getUserById(TestConstants.TEST_USER_ID)).thenReturn(testUser);
        when(userServiceClient.getUsers(anyInt(), anyInt())).thenReturn(userList);
        when(userServiceClient.createUser(any(UserServiceClient.CreateUserRequest.class)))
                .thenReturn(testUser);
        when(userServiceClient.updateUser(eq(TestConstants.TEST_USER_ID),
                any(UserServiceClient.UpdateUserRequest.class))).thenReturn(testUser);
        doNothing().when(userServiceClient).deleteUser(TestConstants.TEST_USER_ID);

        // When & Then - 测试获取用户
        UserServiceClient.UserResponse getUserResult =
                userService.getUserById(TestConstants.TEST_USER_ID);
        assertThat(getUserResult).isNotNull();
        assertThat(getUserResult.getId()).isEqualTo(TestConstants.TEST_USER_ID);

        // When & Then - 测试获取用户列表
        List<UserServiceClient.UserResponse> getUsersResult = userService.getUsers(0, 10);
        assertThat(getUsersResult).hasSize(3);

        // When & Then - 测试创建用户
        UserServiceClient.UserResponse createUserResult = userService.createUser(createRequest);
        assertThat(createUserResult).isNotNull();
        assertThat(createUserResult.getUsername()).isEqualTo(TestConstants.TEST_USERNAME);

        // When & Then - 测试更新用户
        UserServiceClient.UserResponse updateUserResult =
                userService.updateUser(TestConstants.TEST_USER_ID, updateRequest);
        assertThat(updateUserResult).isNotNull();
        assertThat(updateUserResult.getId()).isEqualTo(TestConstants.TEST_USER_ID);

        // When & Then - 测试删除用户
        assertThatCode(() -> userService.deleteUser(TestConstants.TEST_USER_ID))
                .doesNotThrowAnyException();

        // 验证所有方法都被调用
        verify(userServiceClient).getUserById(TestConstants.TEST_USER_ID);
        verify(userServiceClient).getUsers(anyInt(), anyInt());
        verify(userServiceClient).createUser(any(UserServiceClient.CreateUserRequest.class));
        verify(userServiceClient).updateUser(eq(TestConstants.TEST_USER_ID),
                any(UserServiceClient.UpdateUserRequest.class));
        verify(userServiceClient).deleteUser(TestConstants.TEST_USER_ID);
    }

    @Test
    @DisplayName("用户服务异常处理测试")
    void testUserServiceExceptionHandling() {
        // Given
        when(userServiceClient.getUserById(anyLong())).thenThrow(new RuntimeException("外部服务异常"));

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(TestConstants.TEST_USER_ID))
                .isInstanceOf(RuntimeException.class).hasMessage("获取用户信息失败");
    }

    @Test
    @DisplayName("用户服务并发测试")
    void testUserServiceConcurrency() throws InterruptedException {
        // Given
        UserServiceClient.UserResponse testUser = TestDataBuilder.buildUserResponse(
                TestConstants.TEST_USER_ID, TestConstants.TEST_USERNAME, TestConstants.TEST_EMAIL);
        when(userServiceClient.getUserById(TestConstants.TEST_USER_ID)).thenReturn(testUser);

        // When
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                UserServiceClient.UserResponse result =
                        userService.getUserById(TestConstants.TEST_USER_ID);
                assertThat(result).isNotNull();
            });
            threads[i].start();
        }

        // Then
        for (Thread thread : threads) {
            thread.join();
        }

        verify(userServiceClient, times(10)).getUserById(TestConstants.TEST_USER_ID);
    }

    @Test
    @DisplayName("用户服务参数验证测试")
    void testUserServiceParameterValidation() {
        // Given
        UserServiceClient.CreateUserRequest invalidRequest =
                new UserServiceClient.CreateUserRequest();
        invalidRequest.setUsername(""); // 空用户名
        invalidRequest.setEmail("invalid-email"); // 无效邮箱

        // When & Then
        assertThatThrownBy(() -> userService.createUser(invalidRequest))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("用户服务性能测试")
    void testUserServicePerformance() {
        // Given
        UserServiceClient.UserResponse testUser = TestDataBuilder.buildUserResponse(
                TestConstants.TEST_USER_ID, TestConstants.TEST_USERNAME, TestConstants.TEST_EMAIL);
        when(userServiceClient.getUserById(TestConstants.TEST_USER_ID)).thenReturn(testUser);

        // When
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            userService.getUserById(TestConstants.TEST_USER_ID);
        }
        long endTime = System.currentTimeMillis();

        // Then
        long duration = endTime - startTime;
        assertThat(duration).isLessThan(1000); // 应该在1秒内完成
        verify(userServiceClient, times(100)).getUserById(TestConstants.TEST_USER_ID);
    }
}
