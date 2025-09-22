package com.soyokra.sprival.config.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.MicrometerProducerListener;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class SprivalKafkaProducerCustomizer implements DefaultKafkaProducerFactoryCustomizer {
    private final MeterRegistry meterRegistry;

    public SprivalKafkaProducerCustomizer(final MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    @Override
    public void customize(DefaultKafkaProducerFactory<?, ?> producerFactory) {
        producerFactory.addListener(new MicrometerProducerListener<>(meterRegistry));
    }
}
