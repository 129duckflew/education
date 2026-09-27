package cn.duckflew.education.security;

import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final UserRole role;
    private final String username;
    private final String passwordHash;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;

    public UserPrincipal(Long id, UserRole role, String username, String passwordHash,
                         boolean enabled, Set<String> permissions) {
        this.id = id;
        this.role = role;
        this.username = username;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        Set<GrantedAuthority> auths = new LinkedHashSet<>();
        auths.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        permissions.forEach(p -> auths.add(new SimpleGrantedAuthority(p)));
        this.authorities = auths;
    }

    public static UserPrincipal from(User user, Set<String> permissions) {
        String username = user.getUsername() != null ? user.getUsername() : String.valueOf(user.getId());
        return new UserPrincipal(user.getId(), user.getRole(), username,
                user.getPasswordHash(), user.isEnabled(), permissions);
    }

    public Long getId() {
        return id;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
