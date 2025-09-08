package com.soyokra.sprival.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.soyokra.sprival.client.UserServiceClient;

/**
 * 用户服务业务层 演示如何使用Feign客户端进行HTTP调用
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserServiceClient userServiceClient;

    /**
     * 根据ID获取用户信息
     */
    public UserServiceClient.UserResponse getUserById(Long id) {
        logger.info("获取用户信息: id={}", id);

        try {
            UserServiceClient.UserResponse user = userServiceClient.getUserById(id);
            logger.info("成功获取用户信息: id={}, username={}", id, user.getUsername());
            return user;
        } catch (Exception e) {
            logger.error("获取用户信息失败: id={}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("获取用户信息失败", e);
        }
    }

    /**
     * 获取用户列表
     */
    public List<UserServiceClient.UserResponse> getUsers(int page, int size) {
        logger.info("获取用户列表: page={}, size={}", page, size);

        try {
            List<UserServiceClient.UserResponse> users = userServiceClient.getUsers(page, size);
            logger.info("成功获取用户列表: page={}, size={}, count={}", page, size, users.size());
            return users;
        } catch (Exception e) {
            logger.error("获取用户列表失败: page={}, size={}, error={}", page, size, e.getMessage(), e);
            throw new RuntimeException("获取用户列表失败", e);
        }
    }

    /**
     * 创建用户
     */
    public UserServiceClient.UserResponse createUser(UserServiceClient.CreateUserRequest request) {
        logger.info("创建用户: username={}, email={}", request.getUsername(), request.getEmail());

        try {
            UserServiceClient.UserResponse user = userServiceClient.createUser(request);
            logger.info("成功创建用户: id={}, username={}", user.getId(), user.getUsername());
            return user;
        } catch (Exception e) {
            logger.error("创建用户失败: username={}, error={}", request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("创建用户失败", e);
        }
    }

    /**
     * 更新用户信息
     */
    public UserServiceClient.UserResponse updateUser(Long id,
            UserServiceClient.UpdateUserRequest request) {
        logger.info("更新用户信息: id={}, username={}", id, request.getUsername());

        try {
            UserServiceClient.UserResponse user = userServiceClient.updateUser(id, request);
            logger.info("成功更新用户信息: id={}, username={}", id, user.getUsername());
            return user;
        } catch (Exception e) {
            logger.error("更新用户信息失败: id={}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("更新用户信息失败", e);
        }
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        logger.info("删除用户: id={}", id);

        try {
            userServiceClient.deleteUser(id);
            logger.info("成功删除用户: id={}", id);
        } catch (Exception e) {
            logger.error("删除用户失败: id={}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("删除用户失败", e);
        }
    }
}
