package com.soyokra.sprival.config.jetty;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * HTTP安全头配置
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@Order(2)
public class SprivalJettySecurityHeaderCustomizer implements JettyServerCustomizer {

    @Override
    public void customize(Server server) {
        HandlerCollection handlers = new HandlerCollection();

        // 创建安全头处理器
        AbstractHandler securityHandler = new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request,
                    HttpServletResponse response) throws IOException, ServletException {

                // 防止点击劫持攻击
                response.setHeader("X-Frame-Options", "DENY");

                // 防止MIME类型嗅探
                response.setHeader("X-Content-Type-Options", "nosniff");

                // XSS防护
                response.setHeader("X-XSS-Protection", "1; mode=block");

                // 内容安全策略
                response.setHeader("Content-Security-Policy",
                        "default-src 'self'; " + "script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
                                + "style-src 'self' 'unsafe-inline'; "
                                + "img-src 'self' data: https:; " + "font-src 'self'; "
                                + "connect-src 'self'; " + "media-src 'self'; "
                                + "object-src 'none'; " + "child-src 'self'; "
                                + "form-action 'self'; " + "base-uri 'self'");

                // HSTS（仅HTTPS环境）
                if (request.isSecure()) {
                    response.setHeader("Strict-Transport-Security",
                            "max-age=31536000; includeSubDomains; preload");
                }

                // 隐藏服务器信息
                response.setHeader("Server", "Sprival/1.0");

                // 防止缓存敏感信息
                if (target.contains("/api/") && !target.contains("/actuator/")) {
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                }

                // 权限策略
                response.setHeader("Permissions-Policy",
                        "camera=(), microphone=(), geolocation=(), gyroscope=(), magnetometer=()");

                // 推荐人策略
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

                // 继续处理请求
                baseRequest.setHandled(false);
            }
        };

        handlers.addHandler(securityHandler);
        handlers.addHandler(server.getHandler());
        server.setHandler(handlers);
    }
}
