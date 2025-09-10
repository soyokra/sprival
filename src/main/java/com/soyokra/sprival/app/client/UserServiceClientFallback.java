package com.soyokra.sprival.app.client;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务Feign客户端降级处理 当服务不可用时提供默认响应
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
public class UserServiceClientFallback implements UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClientFallback.class);

    @Override
    public UserResponse getUserById(Long id) {
        logger.warn("用户服务不可用，返回默认用户信息: id={}", id);

        UserResponse response = new UserResponse();
        response.setId(id);
        response.setUsername("默认用户");
        response.setEmail("default@example.com");
        response.setPhone("000-0000-0000");
        response.setStatus("INACTIVE");
        response.setCreateTime(System.currentTimeMillis());
        response.setUpdateTime(System.currentTimeMillis());

        return response;
    }

    @Override
    public List<UserResponse> getUsers(int page, int size) {
        logger.warn("用户服务不可用，返回空用户列表: page={}, size={}", page, size);
        return new ArrayList<>();
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        logger.warn("用户服务不可用，无法创建用户: username={}", request.getUsername());

        UserResponse response = new UserResponse();
        response.setId(-1L);
        response.setUsername(request.getUsername());
        response.setEmail(request.getEmail());
        response.setPhone(request.getPhone());
        response.setStatus("FAILED");
        response.setCreateTime(System.currentTimeMillis());
        response.setUpdateTime(System.currentTimeMillis());

        return response;
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        logger.warn("用户服务不可用，无法更新用户: id={}, username={}", id, request.getUsername());

        UserResponse response = new UserResponse();
        response.setId(id);
        response.setUsername(request.getUsername());
        response.setEmail(request.getEmail());
        response.setPhone(request.getPhone());
        response.setStatus("FAILED");
        response.setCreateTime(System.currentTimeMillis());
        response.setUpdateTime(System.currentTimeMillis());

        return response;
    }

    @Override
    public void deleteUser(Long id) {
        logger.warn("用户服务不可用，无法删除用户: id={}", id);
        // 降级处理：静默失败，不抛出异常
    }
}
