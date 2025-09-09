package com.soyokra.sprival.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.soyokra.sprival.client.UserServiceClient;
import com.soyokra.sprival.util.TestConstants;
import com.soyokra.sprival.util.TestDataBuilder;

/**
 * 用户服务单元测试
 * 
 * @author Sprival Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
public class UserServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserService userService;

    private UserServiceClient.UserResponse testUser;
    private UserServiceClient.CreateUserRequest createRequest;
    private UserServiceClient.UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.buildUserResponse(TestConstants.TEST_USER_ID,
                TestConstants.TEST_USERNAME, TestConstants.TEST_EMAIL);
        createRequest = TestDataBuilder.buildCreateUserRequest(TestConstants.TEST_USERNAME,
                TestConstants.TEST_EMAIL);
        updateRequest = TestDataBuilder.buildUpdateUserRequest(TestConstants.TEST_USERNAME,
                TestConstants.TEST_EMAIL);
    }

    @Test
    @DisplayName("根据ID获取用户信息 - 成功")
    void testGetUserById_Success() {
        // Given
        when(userServiceClient.getUserById(TestConstants.TEST_USER_ID)).thenReturn(testUser);

        // When
        UserServiceClient.UserResponse result = userService.getUserById(TestConstants.TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TestConstants.TEST_USER_ID);
        assertThat(result.getUsername()).isEqualTo(TestConstants.TEST_USERNAME);
        assertThat(result.getEmail()).isEqualTo(TestConstants.TEST_EMAIL);
        verify(userServiceClient).getUserById(TestConstants.TEST_USER_ID);
    }

    @Test
    @DisplayName("根据ID获取用户信息 - 用户不存在")
    void testGetUserById_UserNotFound() {
        // Given
        when(userServiceClient.getUserById(anyLong())).thenThrow(new RuntimeException("用户不存在"));

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(TestConstants.TEST_USER_ID))
                .isInstanceOf(RuntimeException.class).hasMessage("获取用户信息失败");
    }

    @Test
    @DisplayName("获取用户列表 - 成功")
    void testGetUsers_Success() {
        // Given
        List<UserServiceClient.UserResponse> expectedUsers = TestDataBuilder.buildUserList(3);
        when(userServiceClient.getUsers(TestConstants.TEST_PAGE, TestConstants.TEST_SIZE))
                .thenReturn(expectedUsers);

        // When
        List<UserServiceClient.UserResponse> result =
                userService.getUsers(TestConstants.TEST_PAGE, TestConstants.TEST_SIZE);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser1");
        verify(userServiceClient).getUsers(TestConstants.TEST_PAGE, TestConstants.TEST_SIZE);
    }

    @Test
    @DisplayName("获取用户列表 - 空列表")
    void testGetUsers_EmptyList() {
        // Given
        when(userServiceClient.getUsers(anyInt(), anyInt())).thenReturn(Arrays.asList());

        // When
        List<UserServiceClient.UserResponse> result =
                userService.getUsers(TestConstants.TEST_PAGE, TestConstants.TEST_SIZE);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("创建用户 - 成功")
    void testCreateUser_Success() {
        // Given
        when(userServiceClient.createUser(any(UserServiceClient.CreateUserRequest.class)))
                .thenReturn(testUser);

        // When
        UserServiceClient.UserResponse result = userService.createUser(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TestConstants.TEST_USER_ID);
        assertThat(result.getUsername()).isEqualTo(TestConstants.TEST_USERNAME);
        verify(userServiceClient).createUser(createRequest);
    }

    @Test
    @DisplayName("创建用户 - 失败")
    void testCreateUser_Failure() {
        // Given
        when(userServiceClient.createUser(any(UserServiceClient.CreateUserRequest.class)))
                .thenThrow(new RuntimeException("创建用户失败"));

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(RuntimeException.class).hasMessage("创建用户失败");
    }

    @Test
    @DisplayName("更新用户信息 - 成功")
    void testUpdateUser_Success() {
        // Given
        when(userServiceClient.updateUser(eq(TestConstants.TEST_USER_ID),
                any(UserServiceClient.UpdateUserRequest.class))).thenReturn(testUser);

        // When
        UserServiceClient.UserResponse result =
                userService.updateUser(TestConstants.TEST_USER_ID, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TestConstants.TEST_USER_ID);
        assertThat(result.getUsername()).isEqualTo(TestConstants.TEST_USERNAME);
        verify(userServiceClient).updateUser(TestConstants.TEST_USER_ID, updateRequest);
    }

    @Test
    @DisplayName("更新用户信息 - 失败")
    void testUpdateUser_Failure() {
        // Given
        when(userServiceClient.updateUser(eq(TestConstants.TEST_USER_ID),
                any(UserServiceClient.UpdateUserRequest.class)))
                        .thenThrow(new RuntimeException("更新用户失败"));

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(TestConstants.TEST_USER_ID, updateRequest))
                .isInstanceOf(RuntimeException.class).hasMessage("更新用户信息失败");
    }

    @Test
    @DisplayName("删除用户 - 成功")
    void testDeleteUser_Success() {
        // Given
        doNothing().when(userServiceClient).deleteUser(TestConstants.TEST_USER_ID);

        // When
        userService.deleteUser(TestConstants.TEST_USER_ID);

        // Then
        verify(userServiceClient).deleteUser(TestConstants.TEST_USER_ID);
    }

    @Test
    @DisplayName("删除用户 - 失败")
    void testDeleteUser_Failure() {
        // Given
        doThrow(new RuntimeException("删除用户失败")).when(userServiceClient)
                .deleteUser(TestConstants.TEST_USER_ID);

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(TestConstants.TEST_USER_ID))
                .isInstanceOf(RuntimeException.class).hasMessage("删除用户失败");
    }

    @Test
    @DisplayName("参数验证 - 空ID")
    void testGetUserById_NullId() {
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("参数验证 - 负数ID")
    void testGetUserById_NegativeId() {
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(-1L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("参数验证 - 空请求对象")
    void testCreateUser_NullRequest() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser(null)).isInstanceOf(RuntimeException.class);
    }
}
