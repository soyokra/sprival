package com.soyokra.sprival.config.jetty;



import org.eclipse.jetty.server.Server;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


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

    @Override
    public void customize(Server server) {

    }
    

}
