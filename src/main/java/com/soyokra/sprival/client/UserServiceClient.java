package com.soyokra.sprival.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.soyokra.sprival.config.http.SprivalHttpClientConfiguration;

/**
 * 用户服务Feign客户端示例 演示如何使用Feign进行HTTP调用
 * 
 * @author Sprival Team
 * @version 1.0
 */
@FeignClient(name = "user-service",
        url = "${sprival.http.client.user-service.url:http://localhost:8081}",
        configuration = SprivalHttpClientConfiguration.class,
        fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);

    /**
     * 获取用户列表
     */
    @GetMapping("/api/users")
    List<UserResponse> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    /**
     * 创建用户
     */
    @PostMapping("/api/users")
    UserResponse createUser(@RequestBody CreateUserRequest request);

    /**
     * 更新用户信息
     */
    @PutMapping("/api/users/{id}")
    UserResponse updateUser(@PathVariable("id") Long id, @RequestBody UpdateUserRequest request);

    /**
     * 删除用户
     */
    @DeleteMapping("/api/users/{id}")
    void deleteUser(@PathVariable("id") Long id);

    /**
     * 用户响应DTO
     */
    class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String status;
        private Long createTime;
        private Long updateTime;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public Long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Long updateTime) {
            this.updateTime = updateTime;
        }
    }

    /**
     * 创建用户请求DTO
     */
    class CreateUserRequest {
        private String username;
        private String email;
        private String phone;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 更新用户请求DTO
     */
    class UpdateUserRequest {
        private String username;
        private String email;
        private String phone;
        private String status;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
