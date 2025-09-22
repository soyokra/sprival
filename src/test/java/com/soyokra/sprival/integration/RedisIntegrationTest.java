package com.soyokra.sprival.integration;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import com.soyokra.sprival.util.TestConstants;
import com.soyokra.sprival.util.TestRedisConfig;

/**
 * Redis集成测试
 * 
 * @author Sprival Team
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestRedisConfig.class})
@DisplayName("Redis集成测试")
public class RedisIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    @DisplayName("Redis基本操作测试")
    void testRedisBasicOperations() {
        // Given
        String key = TestConstants.TEST_CACHE_KEY;
        String value = TestConstants.TEST_CACHE_VALUE;

        // When
        redisTemplate.opsForValue().set(key, value);
        Object result = redisTemplate.opsForValue().get(key);

        // Then
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("Redis过期时间测试")
    void testRedisExpiration() throws InterruptedException {
        // Given
        String key = "expire:test:key";
        String value = "expire:test:value";

        // When
        redisTemplate.opsForValue().set(key, value, 1, TimeUnit.SECONDS);
        Object resultBefore = redisTemplate.opsForValue().get(key);
        Thread.sleep(1100); // 等待过期
        Object resultAfter = redisTemplate.opsForValue().get(key);

        // Then
        assertThat(resultBefore).isEqualTo(value);
        assertThat(resultAfter).isNull();
    }

    @Test
    @DisplayName("Redis哈希操作测试")
    void testRedisHashOperations() {
        // Given
        String hashKey = "hash:test:key";
        String field1 = "field1";
        String field2 = "field2";
        String value1 = "value1";
        String value2 = "value2";

        // When
        redisTemplate.opsForHash().put(hashKey, field1, value1);
        redisTemplate.opsForHash().put(hashKey, field2, value2);
        Object result1 = redisTemplate.opsForHash().get(hashKey, field1);
        Object result2 = redisTemplate.opsForHash().get(hashKey, field2);

        // Then
        assertThat(result1).isEqualTo(value1);
        assertThat(result2).isEqualTo(value2);
    }

    @Test
    @DisplayName("Redis列表操作测试")
    void testRedisListOperations() {
        // Given
        String key = "list:test:key";
        String value1 = "value1";
        String value2 = "value2";
        String value3 = "value3";

        // When
        redisTemplate.opsForList().rightPush(key, value1);
        redisTemplate.opsForList().rightPush(key, value2);
        redisTemplate.opsForList().rightPush(key, value3);
        Object result = redisTemplate.opsForList().leftPop(key);

        // Then
        assertThat(result).isEqualTo(value1);
    }

    @Test
    @DisplayName("Redis集合操作测试")
    void testRedisSetOperations() {
        // Given
        String key = "set:test:key";
        String value1 = "value1";
        String value2 = "value2";

        // When
        redisTemplate.opsForSet().add(key, value1);
        redisTemplate.opsForSet().add(key, value2);
        Boolean isMember1 = redisTemplate.opsForSet().isMember(key, value1);
        Boolean isMember2 = redisTemplate.opsForSet().isMember(key, value2);
        Boolean isMember3 = redisTemplate.opsForSet().isMember(key, "value3");

        // Then
        assertThat(isMember1).isTrue();
        assertThat(isMember2).isTrue();
        assertThat(isMember3).isFalse();
    }

    @Test
    @DisplayName("Redis有序集合操作测试")
    void testRedisZSetOperations() {
        // Given
        String key = "zset:test:key";
        String member1 = "member1";
        String member2 = "member2";
        Double score1 = 1.0;
        Double score2 = 2.0;

        // When
        redisTemplate.opsForZSet().add(key, member1, score1);
        redisTemplate.opsForZSet().add(key, member2, score2);
        Long count = redisTemplate.opsForZSet().count(key, 0.0, 3.0);

        // Then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("StringRedisTemplate操作测试")
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
    @DisplayName("StringRedisTemplate原子操作测试")
    void testStringRedisTemplateAtomicOperations() {
        // Given
        String key = "atomic:test:key";

        // When
        Long result1 = stringRedisTemplate.opsForValue().increment(key);
        Long result2 = stringRedisTemplate.opsForValue().increment(key);
        Long result3 = stringRedisTemplate.opsForValue().decrement(key);

        // Then
        assertThat(result1).isEqualTo(1L);
        assertThat(result2).isEqualTo(2L);
        assertThat(result3).isEqualTo(1L);
    }

    @Test
    @DisplayName("缓存管理器测试")
    void testCacheManager() {
        // Given
        String cacheName = "user";
        String key = "test:key";
        String value = "test:value";

        // When
        cacheManager.getCache(cacheName).put(key, value);
        Object result = cacheManager.getCache(cacheName).get(key, String.class);

        // Then
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("Redis连接测试")
    void testRedisConnection() {
        // When
        String pong = stringRedisTemplate.getConnectionFactory().getConnection().ping();

        // Then
        assertThat(pong).isEqualTo("PONG");
    }

    @Test
    @DisplayName("Redis性能测试")
    void testRedisPerformance() {
        // Given
        String keyPrefix = "perf:test:key:";
        int count = 1000;

        // When
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            stringRedisTemplate.opsForValue().set(keyPrefix + i, "value" + i);
        }
        long endTime = System.currentTimeMillis();

        // Then
        long duration = endTime - startTime;
        assertThat(duration).isLessThan(5000); // 应该在5秒内完成

        // 验证数据
        for (int i = 0; i < count; i++) {
            String result = stringRedisTemplate.opsForValue().get(keyPrefix + i);
            assertThat(result).isEqualTo("value" + i);
        }
    }
}
