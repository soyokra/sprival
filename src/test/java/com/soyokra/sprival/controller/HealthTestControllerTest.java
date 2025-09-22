package com.soyokra.sprival.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 健康测试控制器单元测试
 * 
 * @author Sprival Team
 * @version 1.0
 */
@WebMvcTest(HealthTestController.class)
@ActiveProfiles("test")
@DisplayName("健康测试控制器测试")
public class HealthTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @DisplayName("测试Kafka配置 - Kafka可用")
    void testKafka_KafkaAvailable() throws Exception {
        // Given
        when(kafkaTemplate.getProducerFactory())
                .thenReturn(mock(org.springframework.kafka.core.ProducerFactory.class));
        Map<String, Object> configProps = new HashMap<>();
        configProps.put("bootstrap.servers", "localhost:9092");
        when(kafkaTemplate.getProducerFactory().getConfigurationProperties())
                .thenReturn(configProps);

        // When & Then
        mockMvc.perform(get("/api/test/kafka").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("KafkaTemplate is available"))
                .andExpect(jsonPath("$.kafkaEnabled").value(true))
                .andExpect(jsonPath("$.bootstrapServers").value("localhost:9092"));
    }

    @Test
    @DisplayName("测试Kafka配置 - Kafka不可用")
    void testKafka_KafkaUnavailable() throws Exception {
        // Given
        when(kafkaTemplate).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/test/kafka").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("KafkaTemplate not available"))
                .andExpect(jsonPath("$.kafkaEnabled").value(false));
    }

    @Test
    @DisplayName("测试Kafka配置 - 异常情况")
    void testKafka_Exception() throws Exception {
        // Given
        when(kafkaTemplate.getProducerFactory()).thenThrow(new RuntimeException("Kafka连接失败"));

        // When & Then
        mockMvc.perform(get("/api/test/kafka").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error testing Kafka: Kafka连接失败"))
                .andExpect(jsonPath("$.error").value("RuntimeException"));
    }

    @Test
    @DisplayName("测试应用状态 - 成功")
    void testStatus_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/test/status").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Application is running"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.kafkaAvailable").exists());
    }

    @Test
    @DisplayName("测试应用状态 - 包含时间戳")
    void testStatus_ContainsTimestamp() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/test/status").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.timestamp").isNumber())
                .andExpect(jsonPath("$.timestamp").value(org.hamcrest.Matchers.greaterThan(0L)));
    }

    @Test
    @DisplayName("测试应用状态 - 包含Kafka状态")
    void testStatus_ContainsKafkaStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/test/status").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.kafkaAvailable").isBoolean());
    }

    @Test
    @DisplayName("测试Kafka配置 - 验证调用次数")
    void testKafka_VerifyCallCount() throws Exception {
        // Given
        when(kafkaTemplate.getProducerFactory())
                .thenReturn(mock(org.springframework.kafka.core.ProducerFactory.class));
        when(kafkaTemplate.getProducerFactory().getConfigurationProperties())
                .thenReturn(new HashMap<>());

        // When
        mockMvc.perform(get("/api/test/kafka").contentType(MediaType.APPLICATION_JSON));

        // Then
        verify(kafkaTemplate, times(1)).getProducerFactory();
    }

    @Test
    @DisplayName("测试应用状态 - 验证响应格式")
    void testStatus_VerifyResponseFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/test/status").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap()).andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.kafkaAvailable").exists());
    }
}
