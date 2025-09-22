package com.soyokra.sprival.config.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.MicrometerConsumerListener;
import org.springframework.kafka.core.MicrometerProducerListener;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class SprivalKafkaConsumerCustomizer implements DefaultKafkaConsumerFactoryCustomizer {
    private final MeterRegistry meterRegistry;

    public SprivalKafkaConsumerCustomizer(final MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void customize(DefaultKafkaConsumerFactory<?, ?> consumerFactory) {
        consumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));
    }
}
