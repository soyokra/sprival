package com.soyokra.sprival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.soyokra.sprival.client.UserServiceClient;
import com.soyokra.sprival.service.UserService;

/**
 * 用户控制器 演示HTTP客户端的使用
 * 
 * @author Sprival Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserServiceClient.UserResponse> getUser(@PathVariable Long id) {
        try {
            UserServiceClient.UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取用户列表
     */
    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(userService.getUsers(page, size));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<UserServiceClient.UserResponse> createUser(
            @RequestBody UserServiceClient.CreateUserRequest request) {
        try {
            UserServiceClient.UserResponse user = userService.createUser(request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserServiceClient.UserResponse> updateUser(@PathVariable Long id,
            @RequestBody UserServiceClient.UpdateUserRequest request) {
        try {
            UserServiceClient.UserResponse user = userService.updateUser(id, request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
