package com.soyokra.sprival.app.http.middleware.ratelimiter;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import com.google.common.util.concurrent.RateLimiter;

/**
 * 限流拦截器
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
public class SprivalRateLimitInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SprivalRateLimitInterceptor.class);

    @Autowired
    @Qualifier("globalRateLimiter")
    private RateLimiter globalRateLimiter;

    @Autowired
    @Qualifier("apiRateLimiter")
    private RateLimiter apiRateLimiter;

    @Autowired
    @Qualifier("userActionRateLimiter")
    private RateLimiter userActionRateLimiter;

    @Autowired
    @Qualifier("loginRateLimiter")
    private RateLimiter loginRateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getClientIp(request);

        RateLimiter limiter = selectRateLimiter(uri, method);

        // 尝试获取令牌，最多等待100ms
        if (!limiter.tryAcquire(100, TimeUnit.MILLISECONDS)) {
            logger.warn("Rate limit exceeded for IP: {}, URI: {}, Method: {}", clientIp, uri,
                    method);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            String errorResponse = String.format(
                    "{\"code\":429,\"message\":\"请求过于频繁，请稍后重试\",\"timestamp\":\"%s\",\"path\":\"%s\"}",
                    Instant.now().toString(), uri);
            response.getWriter().write(errorResponse);

            return false;
        }

        return true;
    }

    /**
     * 根据请求URI和方法选择合适的限流器
     */
    private RateLimiter selectRateLimiter(String uri, String method) {
        // 登录相关接口使用最严格的限流
        if (uri.contains("/login") || uri.contains("/auth") || uri.contains("/register")) {
            return loginRateLimiter;
        }

        // 用户操作接口（POST/PUT/DELETE）使用中等限流
        if (("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method))
                && uri.startsWith("/api/v1/")) {
            return userActionRateLimiter;
        }

        // API接口使用API限流器
        if (uri.startsWith("/api/v1/")) {
            return apiRateLimiter;
        }

        // 其他请求使用全局限流器
        return globalRateLimiter;
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        // 检查X-Forwarded-For头（负载均衡器设置）
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        // 检查X-Real-IP头（Nginx设置）
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp;
        }

        // 检查Proxy-Client-IP头
        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        if (StringUtils.hasText(proxyClientIp)) {
            return proxyClientIp;
        }

        // 检查WL-Proxy-Client-IP头（WebLogic）
        String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
        if (StringUtils.hasText(wlProxyClientIp)) {
            return wlProxyClientIp;
        }

        // 使用远程地址
        return request.getRemoteAddr();
    }
}
