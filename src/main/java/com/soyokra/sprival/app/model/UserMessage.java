package com.soyokra.sprival.app.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户消息DTO 用于RabbitMQ消息传递的用户对象
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 消息ID
     */
    private String messageId;

    public UserMessage(Long userId, String username, String email, String operation,
            String content) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.operation = operation;
        this.content = content;
        this.createTime = LocalDateTime.now();
        this.messageId = java.util.UUID.randomUUID().toString();
    }
}
