package com.soyokra.sprival.config.redis;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Redis配置单元测试
 * 
 * @author Sprival Team
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Redis配置测试")
public class SprivalRedisConfigurationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("RedisTemplate Bean 配置正确")
    void testRedisTemplateBean() {
        assertThat(redisTemplate).isNotNull();
        assertThat(redisTemplate.getConnectionFactory()).isNotNull();
    }

    @Test
    @DisplayName("StringRedisTemplate Bean 配置正确")
    void testStringRedisTemplateBean() {
        assertThat(stringRedisTemplate).isNotNull();
        assertThat(stringRedisTemplate.getConnectionFactory()).isNotNull();
    }

    @Test
    @DisplayName("CacheManager Bean 配置正确")
    void testCacheManagerBean() {
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager.getCacheNames()).isNotEmpty();
    }

    @Test
    @DisplayName("RedisTemplate 序列化配置正确")
    void testRedisTemplateSerialization() {
        // Given
        String key = "test:key";
        String value = "test:value";

        // When
        redisTemplate.opsForValue().set(key, value);
        Object result = redisTemplate.opsForValue().get(key);

        // Then
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("StringRedisTemplate 基本操作")
    void testStringRedisTemplateOperations() {
        // Given
        String key = "string:test:key";
        String value = "string:test:value";

        // When
        stringRedisTemplate.opsForValue().set(key, value);
        String result = stringRedisTemplate.opsForValue().get(key);

        // Then
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("缓存管理器包含预定义缓存")
    void testCacheManagerContainsPredefinedCaches() {
        // When
        String[] cacheNames = cacheManager.getCacheNames().toArray(new String[0]);

        // Then
        assertThat(cacheNames).contains("user", "product", "order", "session");
    }

    @Test
    @DisplayName("RedisTemplate 哈希操作")
    void testRedisTemplateHashOperations() {
        // Given
        String hashKey = "hash:test:key";
        String field = "field1";
        String value = "value1";

        // When
        redisTemplate.opsForHash().put(hashKey, field, value);
        Object result = redisTemplate.opsForHash().get(hashKey, field);

        // Then
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("StringRedisTemplate 原子操作")
    void testStringRedisTemplateAtomicOperations() {
        // Given
        String key = "atomic:test:key";

        // When
        Long result = stringRedisTemplate.opsForValue().increment(key);

        // Then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("RedisTemplate 列表操作")
    void testRedisTemplateListOperations() {
        // Given
        String key = "list:test:key";
        String value1 = "value1";
        String value2 = "value2";

        // When
        redisTemplate.opsForList().leftPush(key, value1);
        redisTemplate.opsForList().leftPush(key, value2);
        Object result = redisTemplate.opsForList().rightPop(key);

        // Then
        assertThat(result).isEqualTo(value1);
    }

    @Test
    @DisplayName("RedisTemplate 集合操作")
    void testRedisTemplateSetOperations() {
        // Given
        String key = "set:test:key";
        String value = "value1";

        // When
        redisTemplate.opsForSet().add(key, value);
        Boolean result = redisTemplate.opsForSet().isMember(key, value);

        // Then
        assertThat(result).isTrue();
    }
}
