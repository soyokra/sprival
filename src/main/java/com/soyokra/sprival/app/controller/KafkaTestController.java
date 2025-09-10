package com.soyokra.sprival.app.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.soyokra.sprival.app.data.kafka.KafkaExampleService;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka 测试控制器 提供Kafka功能的测试接口
 * 
 * @author Sprival Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/kafka")
@Slf4j
@ConditionalOnProperty(name = "sprival.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTestController {

    @Autowired
    private KafkaExampleService kafkaExampleService;

    /**
     * 发送简单消息
     */
    @PostMapping("/send/simple")
    public ResponseEntity<Map<String, Object>> sendSimpleMessage(@RequestParam String topic,
            @RequestParam String message) {
        try {
            kafkaExampleService.sendSimpleMessage(topic, message);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "消息发送成功");
            response.put("topic", topic);
            response.put("content", message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("发送简单消息失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "消息发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 发送对象消息
     */
    @PostMapping("/send/object")
    public ResponseEntity<Map<String, Object>> sendObjectMessage(@RequestParam String topic,
            @RequestBody Object data) {
        try {
            kafkaExampleService.sendObjectMessage(topic, data);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "对象消息发送成功");
            response.put("topic", topic);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("发送对象消息失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "对象消息发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 发送到指定分区
     */
    @PostMapping("/send/partition")
    public ResponseEntity<Map<String, Object>> sendToPartition(@RequestParam String topic,
            @RequestParam int partition, @RequestParam String key, @RequestBody Object data) {
        try {
            kafkaExampleService.sendToPartition(topic, partition, key, data);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分区消息发送成功");
            response.put("topic", topic);
            response.put("partition", partition);
            response.put("key", key);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("发送分区消息失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "分区消息发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批量发送消息
     */
    @PostMapping("/send/batch")
    public ResponseEntity<Map<String, Object>> sendBatchMessages(@RequestParam String topic,
            @RequestBody List<Object> messages) {
        try {
            kafkaExampleService.sendBatchMessages(topic, messages);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量消息发送成功");
            response.put("topic", topic);
            response.put("count", messages.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("发送批量消息失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量消息发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 发送测试消息
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> sendTestMessage() {
        try {
            // 发送简单消息
            kafkaExampleService.sendSimpleMessage("test-topic", "Hello Kafka!");

            // 发送对象消息
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", 1);
            userData.put("name", "张三");
            userData.put("email", "zhangsan@example.com");
            kafkaExampleService.sendObjectMessage("user-topic", userData);

            // 发送批量消息
            List<Object> batchMessages = Arrays.asList("消息1", "消息2", "消息3");
            kafkaExampleService.sendBatchMessages("batch-topic", batchMessages);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "测试消息发送成功");
            response.put("topics", Arrays.asList("test-topic", "user-topic", "batch-topic"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("发送测试消息失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "测试消息发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
