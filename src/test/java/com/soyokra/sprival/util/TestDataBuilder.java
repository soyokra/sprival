package com.soyokra.sprival.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.soyokra.sprival.client.UserServiceClient;

/**
 * 测试数据构建器 提供测试数据的快速构建方法
 * 
 * @author Sprival Team
 * @version 1.0
 */
public class TestDataBuilder {

    /**
     * 构建用户响应对象
     */
    public static UserServiceClient.UserResponse buildUserResponse(Long id, String username,
            String email) {
        UserServiceClient.UserResponse user = new UserServiceClient.UserResponse();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone("13800138000");
        user.setStatus("ACTIVE");
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        return user;
    }

    /**
     * 构建创建用户请求对象
     */
    public static UserServiceClient.CreateUserRequest buildCreateUserRequest(String username,
            String email) {
        UserServiceClient.CreateUserRequest request = new UserServiceClient.CreateUserRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPhone("13800138000");
        request.setPassword("password123");
        return request;
    }

    /**
     * 构建更新用户请求对象
     */
    public static UserServiceClient.UpdateUserRequest buildUpdateUserRequest(String username,
            String email) {
        UserServiceClient.UpdateUserRequest request = new UserServiceClient.UpdateUserRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPhone("13800138001");
        request.setStatus("ACTIVE");
        return request;
    }

    /**
     * 构建用户列表
     */
    public static List<UserServiceClient.UserResponse> buildUserList(int count) {
        List<UserServiceClient.UserResponse> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            users.add(buildUserResponse((long) i, "testuser" + i, "test" + i + "@example.com"));
        }
        return users;
    }

    /**
     * 构建产品文档对象
     */
    public static ProductDocument buildProductDocument(Long id, String name, String description,
            Double price) {
        ProductDocument product = new ProductDocument();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(100);
        product.setStatus("ACTIVE");
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        return product;
    }

    /**
     * 构建产品文档列表
     */
    public static List<ProductDocument> buildProductList(int count) {
        List<ProductDocument> products = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            products.add(buildProductDocument((long) i, "测试产品" + i, "产品描述" + i, 99.99 + i));
        }
        return products;
    }

    /**
     * 产品文档内部类
     */
    public static class ProductDocument {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private Integer stock;
        private String status;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        public LocalDateTime getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
        }
    }
}
