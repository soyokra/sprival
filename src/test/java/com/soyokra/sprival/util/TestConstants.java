package com.soyokra.sprival.util;

/**
 * 测试常量 定义测试中使用的常量值
 * 
 * @author Sprival Team
 * @version 1.0
 */
public class TestConstants {

    // 测试用户常量
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PHONE = "13800138000";
    public static final String TEST_PASSWORD = "password123";
    public static final String TEST_STATUS = "ACTIVE";

    // 测试产品常量
    public static final String TEST_PRODUCT_NAME = "测试产品";
    public static final String TEST_PRODUCT_DESCRIPTION = "这是一个测试产品";
    public static final Double TEST_PRODUCT_PRICE = 99.99;
    public static final Integer TEST_PRODUCT_STOCK = 100;

    // 测试订单常量
    public static final String TEST_ORDER_NO = "ORDER001";
    public static final Double TEST_ORDER_AMOUNT = 99.99;
    public static final String TEST_ORDER_STATUS = "PENDING";

    // 测试ID常量
    public static final Long TEST_USER_ID = 1L;
    public static final Long TEST_PRODUCT_ID = 1L;
    public static final Long TEST_ORDER_ID = 1L;

    // 测试分页常量
    public static final int TEST_PAGE = 0;
    public static final int TEST_SIZE = 10;
    public static final int TEST_TOTAL = 100;

    // 测试缓存常量
    public static final String TEST_CACHE_KEY = "test:key";
    public static final String TEST_CACHE_VALUE = "test:value";
    public static final long TEST_CACHE_TTL = 60000L;

    // 测试消息常量
    public static final String TEST_TOPIC = "test.topic";
    public static final String TEST_QUEUE = "test.queue";
    public static final String TEST_EXCHANGE = "test.exchange";
    public static final String TEST_ROUTING_KEY = "test.routing.key";

    // 测试HTTP常量
    public static final String TEST_HTTP_URL = "http://localhost:8080";
    public static final String TEST_HTTP_PATH = "/api/test";
    public static final int TEST_HTTP_TIMEOUT = 5000;

    // 测试数据库常量
    public static final String TEST_DB_NAME = "test_sprival";
    public static final String TEST_TABLE_USER = "sys_user";
    public static final String TEST_TABLE_PRODUCT = "sys_product";
    public static final String TEST_TABLE_ORDER = "sys_order";

    // 测试Redis常量
    public static final String TEST_REDIS_HOST = "localhost";
    public static final int TEST_REDIS_PORT = 6370;
    public static final int TEST_REDIS_DATABASE = 15;

    // 测试MongoDB常量
    public static final String TEST_MONGO_HOST = "localhost";
    public static final int TEST_MONGO_PORT = 27018;
    public static final String TEST_MONGO_DATABASE = "test_sprival";

    // 测试Elasticsearch常量
    public static final String TEST_ES_HOST = "localhost";
    public static final int TEST_ES_PORT = 9200;
    public static final String TEST_ES_INDEX = "test_products";

    // 测试ClickHouse常量
    public static final String TEST_CH_HOST = "localhost";
    public static final int TEST_CH_PORT = 8123;
    public static final String TEST_CH_DATABASE = "test_sprival";

    // 测试监控常量
    public static final String TEST_METRIC_NAME = "test.metric";
    public static final String TEST_METRIC_TAG = "test.tag";
    public static final String TEST_METRIC_VALUE = "test.value";

    // 测试异常常量
    public static final String TEST_ERROR_MESSAGE = "测试错误消息";
    public static final String TEST_ERROR_CODE = "TEST_ERROR";
    public static final int TEST_ERROR_STATUS = 500;

    // 测试时间常量
    public static final long TEST_TIMEOUT = 5000L;
    public static final long TEST_DELAY = 1000L;
    public static final long TEST_INTERVAL = 100L;

    // 测试文件常量
    public static final String TEST_FILE_NAME = "test.txt";
    public static final String TEST_FILE_PATH = "/tmp/test.txt";
    public static final String TEST_FILE_CONTENT = "测试文件内容";

    // 测试JSON常量
    public static final String TEST_JSON_OBJECT = "{\"id\":1,\"name\":\"test\"}";
    public static final String TEST_JSON_ARRAY =
            "[{\"id\":1,\"name\":\"test1\"},{\"id\":2,\"name\":\"test2\"}]";

    // 测试UUID常量
    public static final String TEST_UUID = "123e4567-e89b-12d3-a456-426614174000";
    public static final String TEST_UUID_2 = "123e4567-e89b-12d3-a456-426614174001";

    // 测试正则表达式常量
    public static final String TEST_EMAIL_REGEX =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String TEST_PHONE_REGEX = "^1[3-9]\\d{9}$";
    public static final String TEST_USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";
}
