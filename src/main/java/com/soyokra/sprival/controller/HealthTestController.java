package com.soyokra.sprival.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康测试控制器 用于测试各个组件的健康状态
 * 
 * @author Sprival Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/test")
public class HealthTestController {

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 测试Kafka配置
     */
    @GetMapping("/kafka")
    public ResponseEntity<Map<String, Object>> testKafka() {
        Map<String, Object> response = new HashMap<>();

        try {
            if (kafkaTemplate == null) {
                response.put("success", false);
                response.put("message", "KafkaTemplate not available");
                response.put("kafkaEnabled", false);
            } else {
                response.put("success", true);
                response.put("message", "KafkaTemplate is available");
                response.put("kafkaEnabled", true);
                response.put("bootstrapServers", kafkaTemplate.getProducerFactory()
                        .getConfigurationProperties().get("bootstrap.servers"));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error testing Kafka: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 测试应用状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> testStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Application is running");
        response.put("timestamp", System.currentTimeMillis());
        response.put("kafkaAvailable", kafkaTemplate != null);
        return ResponseEntity.ok(response);
    }
}
