package com.soyokra.sprival.config.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Sprival Kafka 配置类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Configuration
@EnableKafka
@ConditionalOnProperty(prefix = "sprival.kafka", name = "enabled", havingValue = "true",
        matchIfMissing = false)
public class SprivalKafkaConfiguration {

    @Autowired
    private SprivalKafkaProperties kafkaProperties;

    /**
     * 生产者工厂配置
     */
    @Bean
    @ConditionalOnProperty(prefix = "sprival.kafka.producer", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // 基础配置
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // 可靠性配置
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducer().getRetries());
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                kafkaProperties.getProducer().isEnableIdempotence());

        // 性能配置
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG,
                kafkaProperties.getProducer().getBatchSize());
        configProps.put(ProducerConfig.LINGER_MS_CONFIG,
                kafkaProperties.getProducer().getLingerMs());
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG,
                kafkaProperties.getProducer().getBufferMemory());
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,
                kafkaProperties.getProducer().getCompressionType());

        // 事务配置
        if (kafkaProperties.getProducer().getTransactionIdPrefix() != null) {
            configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,
                    kafkaProperties.getProducer().getTransactionIdPrefix()
                            + System.currentTimeMillis());
        }

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * KafkaTemplate 配置
     */
    @Bean
    @ConditionalOnProperty(prefix = "sprival.kafka.producer", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * 消费者工厂配置
     */
    @Bean
    @ConditionalOnProperty(prefix = "sprival.kafka.consumer", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // 基础配置
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        configProps.put(ConsumerConfig.CLIENT_ID_CONFIG,
                kafkaProperties.getConsumer().getClientId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // 偏移量配置
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                kafkaProperties.getConsumer().getAutoOffsetReset());
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                kafkaProperties.getConsumer().isEnableAutoCommit());
        configProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
                kafkaProperties.getConsumer().getAutoCommitIntervalMs());

        // 性能配置
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                kafkaProperties.getConsumer().getMaxPollRecords());
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,
                kafkaProperties.getConsumer().getMaxPollIntervalMs());
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
                kafkaProperties.getConsumer().getSessionTimeoutMs());
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,
                kafkaProperties.getConsumer().getHeartbeatIntervalMs());

        // JSON反序列化配置
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.lang.Object");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * 监听器容器工厂配置
     */
    @Bean
    @ConditionalOnProperty(prefix = "sprival.kafka.consumer", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(kafkaProperties.getConsumer().getConcurrency());

        // 确认模式配置
        ContainerProperties.AckMode ackMode = ContainerProperties.AckMode
                .valueOf(kafkaProperties.getConsumer().getAckMode().toUpperCase());
        factory.getContainerProperties().setAckMode(ackMode);

        return factory;
    }

    /**
     * 批量监听器容器工厂配置
     */
    @Bean
    @ConditionalOnProperty(prefix = "sprival.kafka.consumer", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        factory.setConcurrency(kafkaProperties.getConsumer().getConcurrency());

        // 确认模式配置
        ContainerProperties.AckMode ackMode = ContainerProperties.AckMode
                .valueOf(kafkaProperties.getConsumer().getAckMode().toUpperCase());
        factory.getContainerProperties().setAckMode(ackMode);

        return factory;
    }
}
