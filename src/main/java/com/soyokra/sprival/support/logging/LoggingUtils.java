package com.soyokra.sprival.support.logging;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 日志工具类 提供日志相关的通用工具方法
 * 
 * @author Sprival Team
 * @since 1.0.0
 */
public final class LoggingUtils {

    private static final String UNKNOWN = "unknown";
    private static final String DEFAULT_APP_NAME = "sprival";

    // 缓存主机名，避免重复获取
    private static volatile String cachedHostname;

    private LoggingUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取主机名 首次调用时会缓存结果，后续调用直接返回缓存值
     * 
     * @return 主机名，获取失败时返回 "unknown"
     */
    public static String getHostname() {
        if (cachedHostname == null) {
            synchronized (LoggingUtils.class) {
                if (cachedHostname == null) {
                    try {
                        cachedHostname = InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e) {
                        cachedHostname = UNKNOWN;
                    }
                }
            }
        }
        return cachedHostname;
    }

    /**
     * 获取应用名称 从系统属性 spring.application.name 中获取，如果未设置则返回默认值 "sprival"
     * 
     * @return 应用名称
     */
    public static String getApplicationName() {
        return System.getProperty("spring.application.name", DEFAULT_APP_NAME);
    }

    /**
     * 重置缓存的主机名 主要用于测试场景
     */
    static void resetCache() {
        cachedHostname = null;
    }
}

