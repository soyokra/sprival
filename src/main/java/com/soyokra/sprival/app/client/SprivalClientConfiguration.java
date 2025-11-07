package com.soyokra.sprival.app.client;

import feign.Feign;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SprivalClient Feign 配置
 *
 * @author sprival
 */
@Configuration
public class SprivalClientConfiguration {

    /**
     * 配置 Feign Builder，集成 Resilience4j
     *
     * @param circuitBreakerRegistry 熔断器注册表
     * @param retryRegistry 重试注册表
     * @return Feign Builder
     */
    @Bean
    public Feign.Builder feignBuilder(
            CircuitBreakerRegistry circuitBreakerRegistry,
            RetryRegistry retryRegistry) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("feign");
        Retry retry = retryRegistry.retry("feign");

        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry)
                .build();

        return Resilience4jFeign.builder(decorators);
    }
}
