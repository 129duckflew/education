package cn.duckflew.education.common.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 从当前请求上下文提取客户端信息。
 */
public final class ClientInfo {

    private ClientInfo() {
    }

    public static String ip() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return "unknown";
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public static String device() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return "unknown";
        }
        String agent = request.getHeader("User-Agent");
        return agent == null ? "unknown" : agent;
    }

    private static HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }
}
