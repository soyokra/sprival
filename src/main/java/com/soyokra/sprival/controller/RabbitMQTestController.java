package com.soyokra.sprival.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.soyokra.sprival.dto.UserMessage;
import com.soyokra.sprival.service.RabbitMQExampleService;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 测试控制器 提供RabbitMQ功能的测试接口
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/rabbitmq")
@ConditionalOnProperty(name = "sprival.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQTestController {

    @Autowired
    private RabbitMQExampleService rabbitMQExampleService;

    /**
     * 发送简单消息
     */
    @PostMapping("/send/simple")
    public Map<String, Object> sendSimpleMessage(@RequestParam String message) {
        try {
            rabbitMQExampleService.sendSimpleMessage(message);
            return createSuccessResponse("简单消息发送成功", message);
        } catch (Exception e) {
            log.error("发送简单消息失败", e);
            return createErrorResponse("发送简单消息失败", e.getMessage());
        }
    }

    /**
     * 发送用户消息
     */
    @PostMapping("/send/user")
    public Map<String, Object> sendUserMessage(@RequestBody UserMessage userMessage) {
        try {
            rabbitMQExampleService.sendUserMessage(userMessage);
            return createSuccessResponse("用户消息发送成功", userMessage);
        } catch (Exception e) {
            log.error("发送用户消息失败", e);
            return createErrorResponse("发送用户消息失败", e.getMessage());
        }
    }

    /**
     * 发送消息并等待确认
     */
    @PostMapping("/send/confirm")
    public Map<String, Object> sendMessageWithConfirm(@RequestParam String message) {
        try {
            rabbitMQExampleService.sendMessageWithConfirm(message);
            return createSuccessResponse("消息发送并等待确认成功", message);
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return createErrorResponse("发送消息失败", e.getMessage());
        }
    }

    /**
     * 发送延迟消息
     */
    @PostMapping("/send/delayed")
    public Map<String, Object> sendDelayedMessage(@RequestParam String message,
            @RequestParam(defaultValue = "5000") long delayMs) {
        try {
            rabbitMQExampleService.sendDelayedMessage(message, delayMs);
            Map<String, Object> data = new HashMap<>();
            data.put("message", message);
            data.put("delayMs", delayMs);
            return createSuccessResponse("延迟消息发送成功", data);
        } catch (Exception e) {
            log.error("发送延迟消息失败", e);
            return createErrorResponse("发送延迟消息失败", e.getMessage());
        }
    }

    /**
     * 发送到死信队列
     */
    @PostMapping("/send/dead-letter")
    public Map<String, Object> sendToDeadLetter(@RequestParam String message) {
        try {
            rabbitMQExampleService.sendToDeadLetter(message);
            return createSuccessResponse("死信消息发送成功", message);
        } catch (Exception e) {
            log.error("发送死信消息失败", e);
            return createErrorResponse("发送死信消息失败", e.getMessage());
        }
    }

    /**
     * 批量发送测试消息
     */
    @PostMapping("/send/batch")
    public Map<String, Object> sendBatchMessages(@RequestParam(defaultValue = "10") int count) {
        try {
            for (int i = 0; i < count; i++) {
                String message = "批量消息-" + (i + 1) + "-" + LocalDateTime.now();
                rabbitMQExampleService.sendSimpleMessage(message);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            return createSuccessResponse("批量消息发送成功", data);
        } catch (Exception e) {
            log.error("批量发送消息失败", e);
            return createErrorResponse("批量发送消息失败", e.getMessage());
        }
    }

    /**
     * 创建测试用户消息
     */
    @PostMapping("/send/test-user")
    public Map<String, Object> sendTestUserMessage(
            @RequestParam(defaultValue = "testUser") String username) {
        try {
            UserMessage userMessage =
                    new UserMessage(1L, username, username + "@example.com", "CREATE", "测试用户消息");
            rabbitMQExampleService.sendUserMessage(userMessage);
            return createSuccessResponse("测试用户消息发送成功", userMessage);
        } catch (Exception e) {
            log.error("发送测试用户消息失败", e);
            return createErrorResponse("发送测试用户消息失败", e.getMessage());
        }
    }

    /**
     * 获取RabbitMQ状态信息
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", LocalDateTime.now());
        status.put("service", "RabbitMQ Test Service");
        status.put("status", "UP");
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("sendSimple", "POST /api/rabbitmq/send/simple");
        endpoints.put("sendUser", "POST /api/rabbitmq/send/user");
        endpoints.put("sendConfirm", "POST /api/rabbitmq/send/confirm");
        endpoints.put("sendDelayed", "POST /api/rabbitmq/send/delayed");
        endpoints.put("sendDeadLetter", "POST /api/rabbitmq/send/dead-letter");
        endpoints.put("sendBatch", "POST /api/rabbitmq/send/batch");
        endpoints.put("sendTestUser", "POST /api/rabbitmq/send/test-user");
        status.put("endpoints", endpoints);
        return status;
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    private Map<String, Object> createErrorResponse(String message, String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("error", error);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}
