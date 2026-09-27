package cn.duckflew.education.security;

import cn.duckflew.education.user.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 获取当前登录用户的便捷入口（替代 Sa-Token 的 StpUtil）。
 */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static Optional<UserPrincipal> find() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal);
    }

    public static Long id() {
        return find().map(UserPrincipal::getId)
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("未登录"));
    }

    public static Long idOrNull() {
        return find().map(UserPrincipal::getId).orElse(null);
    }

    public static UserRole role() {
        return find().map(UserPrincipal::getRole)
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("未登录"));
    }
}
