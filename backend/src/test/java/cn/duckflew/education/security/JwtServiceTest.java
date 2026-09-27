package cn.duckflew.education.security;

import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(new JwtProperties(
            "education-test",
            Duration.ofMinutes(30),
            Duration.ofDays(14),
            "unit-test-secret-must-be-long-enough-for-hs256"));

    @Test
    void issuesAndDecodesAccessToken() {
        User user = new User();
        user.setId(42L);
        user.setUsername("alice");
        user.setNickname("Alice");
        user.setRole(UserRole.PROFESSOR);

        String token = jwtService.issueAccessToken(user);
        Jwt jwt = jwtService.decode(token);

        assertThat(jwt.getSubject()).isEqualTo("42");
        assertThat(jwt.getClaimAsString("typ")).isEqualTo(JwtService.TYPE_ACCESS);
        assertThat(jwt.getClaimAsString("role")).isEqualTo("PROFESSOR");
        assertThat(jwt.getClaimAsString("name")).isEqualTo("Alice");
        assertThat(jwt.getExpiresAt()).isAfter(jwt.getIssuedAt());
    }

    @Test
    void refreshTokenHasRefreshType() {
        User user = new User();
        user.setId(7L);
        user.setRole(UserRole.USER);

        Jwt jwt = jwtService.decode(jwtService.issueRefreshToken(user));

        assertThat(jwt.getSubject()).isEqualTo("7");
        assertThat(jwt.getClaimAsString("typ")).isEqualTo(JwtService.TYPE_REFRESH);
    }

    @Test
    void rejectsTokenSignedWithAnotherSecret() {
        JwtService other = new JwtService(new JwtProperties(
                "education-test",
                Duration.ofMinutes(30),
                Duration.ofDays(14),
                "a-completely-different-secret-value-also-long-enough"));
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);

        String foreignToken = other.issueAccessToken(user);

        assertThatThrownBy(() -> jwtService.decode(foreignToken))
                .isInstanceOf(Exception.class);
    }
}
