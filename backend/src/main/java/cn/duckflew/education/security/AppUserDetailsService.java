package cn.duckflew.education.security;

import cn.duckflew.education.admin.PermissionResolver;
import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 按用户 id（JWT subject）加载主体。
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PermissionResolver permissionResolver;

    public AppUserDetailsService(UserRepository userRepository, PermissionResolver permissionResolver) {
        this.userRepository = userRepository;
        this.permissionResolver = permissionResolver;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + userId));
        Set<String> permissions = user.getRole() == cn.duckflew.education.user.UserRole.ADMIN
                ? permissionResolver.permissionsOf(user.getId())
                : Set.of();
        return UserPrincipal.from(user, permissions);
    }
}
