package com.soyokra.sprival.config.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.MicrometerConsumerListener;
import org.springframework.kafka.core.MicrometerProducerListener;

@Configuration
public class SprivalKafkaConfiguration {

    private final MeterRegistry meterRegistry;

    public SprivalKafkaConfiguration(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public DefaultKafkaProducerFactoryCustomizer kafkaProducerFactoryCustomizer() {
        return producerFactory -> producerFactory.addListener(new MicrometerProducerListener<>(meterRegistry));
    }

    @Bean
    public DefaultKafkaConsumerFactoryCustomizer kafkaConsumerFactoryCustomizer() {
        return consumerFactory -> consumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));
    }
}
