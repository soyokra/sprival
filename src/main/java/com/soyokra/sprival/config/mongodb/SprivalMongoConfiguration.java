package com.soyokra.sprival.config.mongodb;

import com.mongodb.MongoClientSettings;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommandListener;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsConnectionPoolListener;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SprivalMongoConfiguration {

    private final MeterRegistry meterRegistry;

    public SprivalMongoConfiguration(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return builder -> builder.addCommandListener(new MongoMetricsCommandListener(meterRegistry));
    }

    @Bean
    public MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder().applyToConnectionPoolSettings(builder -> builder.addConnectionPoolListener(new MongoMetricsConnectionPoolListener(meterRegistry))).build();
    }
}