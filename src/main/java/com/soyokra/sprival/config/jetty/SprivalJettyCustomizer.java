package com.soyokra.sprival.config.jetty;


import com.soyokra.sprival.support.logging.KafkaRequestLog;
import com.soyokra.sprival.support.logging.LogOutputTarget;
import com.soyokra.sprival.support.logging.SprivalLoggingProperties;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.RequestLogWriter;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Sprival Jetty服务器自定义配置
 * 支持根据配置动态选择访问日志输出目标（文件/Kafka/同时输出）
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@Order(1)
public class SprivalJettyCustomizer implements JettyServerCustomizer {
    
    private static final Logger log = LoggerFactory.getLogger(SprivalJettyCustomizer.class);
    
    @Autowired
    private SprivalLoggingProperties loggingProperties;
    
    @Value("${server.jetty.accesslog.enabled:true}")
    private boolean accessLogEnabled;
    
    @Value("${server.jetty.accesslog.filename:logs/jetty-access.log}")
    private String accessLogFilename;
    
    @Value("${server.jetty.accesslog.format:EXTENDED_NCSA}")
    private String accessLogFormat;
    
    @Value("${server.jetty.accesslog.retain-days:7}")
    private int retainDays;
    
    @Value("${server.jetty.accesslog.ignore-paths:}")
    private String ignorePathsConfig;
    
    @Value("${server.jetty.accesslog.append:true}")
    private boolean append;

    @Override
    public void customize(Server server) {
        if (!accessLogEnabled) {
            log.info("Jetty access log is disabled");
            return;
        }
        
        LogOutputTarget outputTarget = loggingProperties.getJettyAccess().getOutputTarget();
        log.info("Jetty access log output target: {}", outputTarget.getValue());
        
        Set<String> ignorePaths = parseIgnorePaths(ignorePathsConfig);
        
        try {
            // 根据配置选择日志输出方式
            if (outputTarget == LogOutputTarget.FILE) {
                // 仅输出到文件（使用Spring Boot默认配置）
                log.info("Jetty access log will write to file only: {}", accessLogFilename);
            } else if (outputTarget == LogOutputTarget.KAFKA) {
                // 仅输出到Kafka
                KafkaRequestLog kafkaRequestLog = new KafkaRequestLog(loggingProperties, ignorePaths);
                server.setRequestLog(kafkaRequestLog);
                log.info("Jetty access log will write to Kafka only, topic: {}", 
                        loggingProperties.getJettyAccess().getTopic());
            } else if (outputTarget == LogOutputTarget.BOTH) {
                // 同时输出到文件和Kafka
                // 创建文件日志
                RequestLogWriter fileLogWriter = new RequestLogWriter(accessLogFilename);
                fileLogWriter.setAppend(append);
                fileLogWriter.setRetainDays(retainDays);
                CustomRequestLog fileRequestLog = new CustomRequestLog(fileLogWriter, 
                        convertFormat(accessLogFormat));
                
                // 创建Kafka日志
                KafkaRequestLog kafkaRequestLog = new KafkaRequestLog(loggingProperties, ignorePaths);
                
                // 创建组合日志
                CombinedRequestLog combinedLog = new CombinedRequestLog(fileRequestLog, kafkaRequestLog);
                server.setRequestLog(combinedLog);
                log.info("Jetty access log will write to both file ({}) and Kafka (topic: {})", 
                        accessLogFilename, loggingProperties.getJettyAccess().getTopic());
            }
        } catch (Exception e) {
            log.error("Failed to configure Jetty access log", e);
        }
    }
    
    /**
     * 解析忽略路径配置
     */
    private Set<String> parseIgnorePaths(String ignorePathsConfig) {
        Set<String> ignorePaths = new HashSet<>();
        if (ignorePathsConfig != null && !ignorePathsConfig.trim().isEmpty()) {
            String[] paths = ignorePathsConfig.split(",");
            for (String path : paths) {
                String trimmedPath = path.trim();
                if (!trimmedPath.isEmpty()) {
                    ignorePaths.add(trimmedPath);
                }
            }
        }
        return ignorePaths;
    }
    
    /**
     * 转换日志格式字符串
     */
    private String convertFormat(String format) {
        if ("EXTENDED_NCSA".equalsIgnoreCase(format)) {
            return CustomRequestLog.EXTENDED_NCSA_FORMAT;
        } else if ("NCSA".equalsIgnoreCase(format)) {
            return CustomRequestLog.NCSA_FORMAT;
        }
        return format;
    }
    
    /**
     * 组合请求日志
     * 同时将日志写入文件和Kafka
     */
    private static class CombinedRequestLog implements RequestLog {
        private final RequestLog fileLog;
        private final RequestLog kafkaLog;
        
        public CombinedRequestLog(RequestLog fileLog, RequestLog kafkaLog) {
            this.fileLog = fileLog;
            this.kafkaLog = kafkaLog;
        }
        
        @Override
        public void log(org.eclipse.jetty.server.Request request, org.eclipse.jetty.server.Response response) {
            // 写入文件
            fileLog.log(request, response);
            // 写入Kafka
            kafkaLog.log(request, response);
        }
    }
}
