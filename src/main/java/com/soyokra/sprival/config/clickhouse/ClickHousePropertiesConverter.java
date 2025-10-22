package com.soyokra.sprival.config.clickhouse;

import java.util.Map;
import java.util.Properties;
import com.clickhouse.client.ClickHouseProtocol;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

/**
 * ClickHouse 属性转换器
 * <p>
 * 用于将 URL 参数、Properties 或 Map 配置转换为 ClickHouseProperties 对象
 * </p>
 *
 * @author Sprival Team
 * @version 1.0
 */
public class ClickHousePropertiesConverter {

    /**
     * 从 Properties 转换为 ClickHouseProperties
     *
     * @param properties 属性配置
     * @return ClickHouseProperties 对象
     */
    public static ClickHouseProperties fromProperties(Properties properties) {
        ClickHouseProperties clickHouseProperties = new ClickHouseProperties(properties);
        return clickHouseProperties;
    }

    /**
     * 从 Map 转换为 ClickHouseProperties
     *
     * @param params 参数 Map
     * @return ClickHouseProperties 对象
     */
    public static ClickHouseProperties fromMap(Map<String, String> params) {
        Properties properties = new Properties();
        properties.putAll(params);
        return fromProperties(properties);
    }

    /**
     * 创建构建器
     *
     * @return ClickHousePropertiesBuilder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * ClickHouseProperties 构建器
     * <p>
     * 提供流式 API 来构建 ClickHouseProperties 对象
     * </p>
     */
    public static class Builder {
        private final ClickHouseProperties properties;

        private Builder() {
            this.properties = new ClickHouseProperties();
        }

        // ==================== 连接配置 ====================

        /**
         * 设置主机地址
         */
        public Builder host(String host) {
            properties.setHost(host);
            return this;
        }

        /**
         * 设置端口
         */
        public Builder port(int port) {
            properties.setPort(port);
            return this;
        }

        /**
         * 设置数据库名称
         */
        public Builder database(String database) {
            properties.setDatabase(database);
            return this;
        }

        /**
         * 设置用户名
         */
        public Builder user(String user) {
            properties.setUser(user);
            return this;
        }

        /**
         * 设置密码
         */
        public Builder password(String password) {
            properties.setPassword(password);
            return this;
        }

        /**
         * 设置协议
         */
        public Builder protocol(ClickHouseProtocol protocol) {
            properties.setProtocol(protocol);
            return this;
        }

        /**
         * 设置协议（字符串）
         */
        public Builder protocol(String protocol) {
            if (protocol != null && !protocol.isEmpty()) {
                properties.setProtocol(ClickHouseProtocol.valueOf(protocol.toUpperCase()));
            }
            return this;
        }

        /**
         * 设置路径
         */
        public Builder path(String path) {
            properties.setPath(path);
            return this;
        }

        /**
         * 设置是否使用路径作为数据库名
         */
        public Builder usePathAsDb(boolean usePathAsDb) {
            properties.setUsePathAsDb(usePathAsDb);
            return this;
        }

        // ==================== 超时配置 ====================

        /**
         * 设置 Socket 超时时间（毫秒）
         */
        public Builder socketTimeout(int socketTimeout) {
            properties.setSocketTimeout(socketTimeout);
            return this;
        }

        /**
         * 设置连接超时时间（毫秒）
         */
        public Builder connectionTimeout(int connectionTimeout) {
            properties.setConnectionTimeout(connectionTimeout);
            return this;
        }

        /**
         * 设置数据传输超时时间（毫秒）
         */
        public Builder dataTransferTimeout(int dataTransferTimeout) {
            properties.setDataTransferTimeout(dataTransferTimeout);
            return this;
        }

        /**
         * 设置连接存活时间（毫秒）
         */
        public Builder timeToLiveMillis(int timeToLiveMillis) {
            properties.setTimeToLiveMillis(timeToLiveMillis);
            return this;
        }

        /**
         * 设置空闲后验证时间（毫秒）
         */
        public Builder validateAfterInactivityMillis(int validateAfterInactivityMillis) {
            properties.setValidateAfterInactivityMillis(validateAfterInactivityMillis);
            return this;
        }

        // ==================== 连接池配置 ====================

        /**
         * 设置每个路由的最大连接数
         */
        public Builder defaultMaxPerRoute(int defaultMaxPerRoute) {
            properties.setDefaultMaxPerRoute(defaultMaxPerRoute);
            return this;
        }

        /**
         * 设置最大总连接数
         */
        public Builder maxTotal(int maxTotal) {
            properties.setMaxTotal(maxTotal);
            return this;
        }

        /**
         * 设置最大重试次数
         */
        public Builder maxRetries(int maxRetries) {
            properties.setMaxRetries(maxRetries);
            return this;
        }

        // ==================== 缓冲区配置 ====================

        /**
         * 设置缓冲区大小
         */
        public Builder bufferSize(int bufferSize) {
            properties.setBufferSize(bufferSize);
            return this;
        }

        /**
         * 设置 Apache HTTP 客户端缓冲区大小
         */
        public Builder apacheBufferSize(int apacheBufferSize) {
            properties.setApacheBufferSize(apacheBufferSize);
            return this;
        }

        /**
         * 设置最大压缩缓冲区大小
         */
        public Builder maxCompressBufferSize(int maxCompressBufferSize) {
            properties.setMaxCompressBufferSize(maxCompressBufferSize);
            return this;
        }

        // ==================== SSL 配置 ====================

        /**
         * 设置是否启用 SSL
         */
        public Builder ssl(boolean ssl) {
            properties.setSsl(ssl);
            return this;
        }

        /**
         * 设置 SSL 根证书路径
         */
        public Builder sslRootCertificate(String sslRootCertificate) {
            properties.setSslRootCertificate(sslRootCertificate);
            return this;
        }

        /**
         * 设置 SSL 模式
         */
        public Builder sslMode(String sslMode) {
            properties.setSslMode(sslMode);
            return this;
        }

        // ==================== 异步和压缩配置 ====================

        /**
         * 设置是否异步执行
         */
        public Builder async(boolean async) {
            properties.setAsync(async);
            return this;
        }

        /**
         * 设置是否压缩
         */
        public Builder compress(boolean compress) {
            properties.setCompress(compress);
            return this;
        }

        /**
         * 设置是否解压缩
         */
        public Builder decompress(boolean decompress) {
            properties.setDecompress(decompress);
            return this;
        }

        // ==================== 重定向配置 ====================

        /**
         * 设置最大重定向次数
         */
        public Builder maxRedirects(int maxRedirects) {
            properties.setMaxRedirects(maxRedirects);
            return this;
        }

        /**
         * 设置是否检查重定向
         */
        public Builder checkForRedirects(boolean checkForRedirects) {
            properties.setCheckForRedirects(checkForRedirects);
            return this;
        }

        // ==================== 时区配置 ====================

        /**
         * 设置是否使用服务器时区
         */
        public Builder useServerTimeZone(boolean useServerTimeZone) {
            properties.setUseServerTimeZone(useServerTimeZone);
            return this;
        }

        /**
         * 设置使用的时区
         */
        public Builder useTimeZone(String useTimeZone) {
            properties.setUseTimeZone(useTimeZone);
            return this;
        }

        /**
         * 设置日期是否使用服务器时区
         */
        public Builder useServerTimeZoneForDates(boolean useServerTimeZoneForDates) {
            properties.setUseServerTimeZoneForDates(useServerTimeZoneForDates);
            return this;
        }

        // ==================== 其他配置 ====================

        /**
         * 设置是否在数组中使用对象
         */
        public Builder useObjectsInArrays(boolean useObjectsInArrays) {
            properties.setUseObjectsInArrays(useObjectsInArrays);
            return this;
        }

        /**
         * 设置是否使用共享 Cookie 存储
         */
        public Builder useSharedCookieStore(boolean useSharedCookieStore) {
            properties.setUseSharedCookieStore(useSharedCookieStore);
            return this;
        }

        /**
         * 设置客户端名称
         */
        public Builder clientName(String clientName) {
            properties.setClientName(clientName);
            return this;
        }

        // ==================== 查询性能配置 ====================

        /**
         * 设置最大并行副本数
         */
        public Builder maxParallelReplicas(Integer maxParallelReplicas) {
            properties.setMaxParallelReplicas(maxParallelReplicas);
            return this;
        }

        /**
         * 设置每个插入块的最大分区数
         */
        public Builder maxPartitionsPerInsertBlock(Integer maxPartitionsPerInsertBlock) {
            properties.setMaxPartitionsPerInsertBlock(maxPartitionsPerInsertBlock);
            return this;
        }

        /**
         * 设置总计模式
         */
        public Builder totalsMode(String totalsMode) {
            properties.setTotalsMode(totalsMode);
            return this;
        }

        /**
         * 设置配额键
         */
        public Builder quotaKey(String quotaKey) {
            properties.setQuotaKey(quotaKey);
            return this;
        }

        /**
         * 设置优先级
         */
        public Builder priority(Integer priority) {
            properties.setPriority(priority);
            return this;
        }

        /**
         * 设置是否返回极值
         */
        public Builder extremes(boolean extremes) {
            properties.setExtremes(extremes);
            return this;
        }

        /**
         * 设置最大线程数
         */
        public Builder maxThreads(Integer maxThreads) {
            properties.setMaxThreads(maxThreads);
            return this;
        }

        /**
         * 设置最大执行时间（秒）
         */
        public Builder maxExecutionTime(Integer maxExecutionTime) {
            properties.setMaxExecutionTime(maxExecutionTime);
            return this;
        }

        /**
         * 设置最大块大小
         */
        public Builder maxBlockSize(Integer maxBlockSize) {
            properties.setMaxBlockSize(maxBlockSize);
            return this;
        }

        /**
         * 设置分组的最大行数
         */
        public Builder maxRowsToGroupBy(Integer maxRowsToGroupBy) {
            properties.setMaxRowsToGroupBy(maxRowsToGroupBy);
            return this;
        }

        /**
         * 设置配置文件
         */
        public Builder profile(String profile) {
            properties.setProfile(profile);
            return this;
        }

        /**
         * 设置 HTTP 授权头
         */
        public Builder httpAuthorization(String httpAuthorization) {
            properties.setHttpAuthorization(httpAuthorization);
            return this;
        }

        /**
         * 设置是否启用分布式聚合内存高效模式
         */
        public Builder distributedAggregationMemoryEfficient(
                boolean distributedAggregationMemoryEfficient) {
            properties.setDistributedAggregationMemoryEfficient(
                    distributedAggregationMemoryEfficient);
            return this;
        }

        // ==================== 内存配置 ====================

        /**
         * 设置外部分组前的最大字节数
         */
        public Builder maxBytesBeforeExternalGroupBy(Long maxBytesBeforeExternalGroupBy) {
            properties.setMaxBytesBeforeExternalGroupBy(maxBytesBeforeExternalGroupBy);
            return this;
        }

        /**
         * 设置外部排序前的最大字节数
         */
        public Builder maxBytesBeforeExternalSort(Long maxBytesBeforeExternalSort) {
            properties.setMaxBytesBeforeExternalSort(maxBytesBeforeExternalSort);
            return this;
        }

        /**
         * 设置最大内存使用量
         */
        public Builder maxMemoryUsage(Long maxMemoryUsage) {
            properties.setMaxMemoryUsage(maxMemoryUsage);
            return this;
        }

        /**
         * 设置单个用户的最大内存使用量
         */
        public Builder maxMemoryUsageForUser(Long maxMemoryUsageForUser) {
            properties.setMaxMemoryUsageForUser(maxMemoryUsageForUser);
            return this;
        }

        /**
         * 设置所有查询的最大内存使用量
         */
        public Builder maxMemoryUsageForAllQueries(Long maxMemoryUsageForAllQueries) {
            properties.setMaxMemoryUsageForAllQueries(maxMemoryUsageForAllQueries);
            return this;
        }

        /**
         * 设置首选块大小（字节）
         */
        public Builder preferredBlockSizeBytes(Long preferredBlockSizeBytes) {
            properties.setPreferredBlockSizeBytes(preferredBlockSizeBytes);
            return this;
        }

        /**
         * 设置最大查询大小
         */
        public Builder maxQuerySize(Long maxQuerySize) {
            properties.setMaxQuerySize(maxQuerySize);
            return this;
        }

        /**
         * 设置最大 AST 元素数
         */
        public Builder maxAstElements(Long maxAstElements) {
            properties.setMaxAstElements(maxAstElements);
            return this;
        }

        // ==================== 会话配置 ====================

        /**
         * 设置是否检查会话
         */
        public Builder sessionCheck(boolean sessionCheck) {
            properties.setSessionCheck(sessionCheck);
            return this;
        }

        /**
         * 设置会话 ID
         */
        public Builder sessionId(String sessionId) {
            properties.setSessionId(sessionId);
            return this;
        }

        /**
         * 设置会话超时时间
         */
        public Builder sessionTimeout(Long sessionTimeout) {
            properties.setSessionTimeout(sessionTimeout);
            return this;
        }

        // ==================== 插入配置 ====================

        /**
         * 设置插入仲裁数
         */
        public Builder insertQuorum(Long insertQuorum) {
            properties.setInsertQuorum(insertQuorum);
            return this;
        }

        /**
         * 设置插入仲裁超时时间
         */
        public Builder insertQuorumTimeout(Long insertQuorumTimeout) {
            properties.setInsertQuorumTimeout(insertQuorumTimeout);
            return this;
        }

        /**
         * 设置选择顺序一致性
         */
        public Builder selectSequentialConsistency(Long selectSequentialConsistency) {
            properties.setSelectSequentialConsistency(selectSequentialConsistency);
            return this;
        }

        /**
         * 设置是否启用优化谓词表达式
         */
        public Builder enableOptimizePredicateExpression(
                Boolean enableOptimizePredicateExpression) {
            properties.setEnableOptimizePredicateExpression(enableOptimizePredicateExpression);
            return this;
        }

        /**
         * 设置最大插入块大小
         */
        public Builder maxInsertBlockSize(Long maxInsertBlockSize) {
            properties.setMaxInsertBlockSize(maxInsertBlockSize);
            return this;
        }

        /**
         * 设置是否插入去重
         */
        public Builder insertDeduplicate(Boolean insertDeduplicate) {
            properties.setInsertDeduplicate(insertDeduplicate);
            return this;
        }

        /**
         * 设置是否分布式同步插入
         */
        public Builder insertDistributedSync(Boolean insertDistributedSync) {
            properties.setInsertDistributedSync(insertDistributedSync);
            return this;
        }

        /**
         * 设置任意连接是否区分右表键
         */
        public Builder anyJoinDistinctRightTableKeys(Boolean anyJoinDistinctRightTableKeys) {
            properties.setAnyJoinDistinctRightTableKeys(anyJoinDistinctRightTableKeys);
            return this;
        }

        // ==================== HTTP 配置 ====================

        /**
         * 设置是否在 HTTP 头中发送进度
         */
        public Builder sendProgressInHttpHeaders(Boolean sendProgressInHttpHeaders) {
            properties.setSendProgressInHttpHeaders(sendProgressInHttpHeaders);
            return this;
        }

        /**
         * 设置是否等待查询结束
         */
        public Builder waitEndOfQuery(Boolean waitEndOfQuery) {
            properties.setWaitEndOfQuery(waitEndOfQuery);
            return this;
        }

        // ==================== 构建方法 ====================

        /**
         * 构建 ClickHouseProperties 对象
         *
         * @return ClickHouseProperties 实例
         */
        public ClickHouseProperties build() {
            return properties;
        }

        /**
         * 从现有的 ClickHouseProperties 复制配置
         *
         * @param source 源 ClickHouseProperties
         * @return Builder 实例
         */
        public Builder from(ClickHouseProperties source) {
            return this; // 可以通过 new ClickHouseProperties(source) 实现
        }

        /**
         * 从 Properties 加载配置
         *
         * @param props Properties 对象
         * @return Builder 实例
         */
        public Builder fromProperties(Properties props) {
            // 遍历 Properties 并根据键名调用相应的 setter
            props.forEach((key, value) -> {
                String keyStr = key.toString();
                String valueStr = value.toString();
                applyProperty(keyStr, valueStr);
            });
            return this;
        }

        /**
         * 从 Map 加载配置
         *
         * @param params 参数 Map
         * @return Builder 实例
         */
        public Builder fromMap(Map<String, String> params) {
            params.forEach(this::applyProperty);
            return this;
        }

        /**
         * 应用单个属性
         */
        private void applyProperty(String key, String value) {
            if (value == null || value.trim().isEmpty()) {
                return;
            }

            try {
                switch (key.toLowerCase()) {
                    // 连接配置
                    case "host":
                        host(value);
                        break;
                    case "port":
                        port(Integer.parseInt(value));
                        break;
                    case "database":
                        database(value);
                        break;
                    case "user":
                    case "username":
                        user(value);
                        break;
                    case "password":
                        password(value);
                        break;
                    case "protocol":
                        protocol(value);
                        break;
                    case "path":
                        path(value);
                        break;
                    case "usepathasdb":
                    case "use_path_as_db":
                        usePathAsDb(Boolean.parseBoolean(value));
                        break;

                    // 超时配置
                    case "sockettimeout":
                    case "socket_timeout":
                        socketTimeout(Integer.parseInt(value));
                        break;
                    case "connectiontimeout":
                    case "connection_timeout":
                        connectionTimeout(Integer.parseInt(value));
                        break;
                    case "datatransfertimeout":
                    case "data_transfer_timeout":
                        dataTransferTimeout(Integer.parseInt(value));
                        break;

                    // 连接池配置
                    case "defaultmaxperroute":
                    case "default_max_per_route":
                        defaultMaxPerRoute(Integer.parseInt(value));
                        break;
                    case "maxtotal":
                    case "max_total":
                        maxTotal(Integer.parseInt(value));
                        break;
                    case "maxretries":
                    case "max_retries":
                        maxRetries(Integer.parseInt(value));
                        break;

                    // 缓冲区配置
                    case "buffersize":
                    case "buffer_size":
                        bufferSize(Integer.parseInt(value));
                        break;
                    case "apachebuffersize":
                    case "apache_buffer_size":
                        apacheBufferSize(Integer.parseInt(value));
                        break;

                    // SSL 配置
                    case "ssl":
                        ssl(Boolean.parseBoolean(value));
                        break;
                    case "sslrootcertificate":
                    case "ssl_root_certificate":
                        sslRootCertificate(value);
                        break;
                    case "sslmode":
                    case "ssl_mode":
                        sslMode(value);
                        break;

                    // 压缩配置
                    case "compress":
                        compress(Boolean.parseBoolean(value));
                        break;
                    case "decompress":
                        decompress(Boolean.parseBoolean(value));
                        break;

                    // 性能配置
                    case "maxthreads":
                    case "max_threads":
                        maxThreads(Integer.parseInt(value));
                        break;
                    case "maxexecutiontime":
                    case "max_execution_time":
                        maxExecutionTime(Integer.parseInt(value));
                        break;
                    case "maxblocksize":
                    case "max_block_size":
                        maxBlockSize(Integer.parseInt(value));
                        break;

                    // 其他常用配置
                    case "clientname":
                    case "client_name":
                        clientName(value);
                        break;

                    // 如果不是已知的属性，忽略
                    default:
                        // 可以记录日志或忽略
                        break;
                }
            } catch (Exception e) {
                // 处理转换异常，可以记录日志
                // log.warn("Failed to apply property {}: {}", key, e.getMessage());
            }
        }
    }
}

