package cn.duckflew.education.security;

import cn.duckflew.education.user.User;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * 基于 HMAC-SHA256 的 JWT 签发与校验。无状态，不依赖 Redis。
 */
@Service
public class JwtService {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final JwtProperties properties;
    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        SecretKeySpec key = new SecretKeySpec(
                properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(key));
        this.decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    public String issueAccessToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiresAt(now.plus(properties.accessTokenTtl()))
                .claim("typ", TYPE_ACCESS)
                .claim("role", user.getRole().name())
                .claim("name", displayName(user))
                .build();
        return encode(claims);
    }

    public String issueRefreshToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiresAt(now.plus(properties.refreshTokenTtl()))
                .claim("typ", TYPE_REFRESH)
                .build();
        return encode(claims);
    }

    /**
     * 解析并校验签名与过期时间。
     *
     * @throws JwtException 签名/过期/格式非法时抛出
     */
    public Jwt decode(String token) {
        return decoder.decode(token);
    }

    public long accessTokenTtlSeconds() {
        return properties.accessTokenTtl().toSeconds();
    }

    /**
     * 从 access token 解析用户 id，供 SSE 等无法设置请求头的场景使用。
     *
     * @throws JwtException token 非法或类型不匹配
     */
    public Long userIdFromAccessToken(String token) {
        Jwt jwt = decode(token);
        if (!TYPE_ACCESS.equals(jwt.getClaimAsString("typ"))) {
            throw new JwtException("不是 access token");
        }
        return Long.valueOf(jwt.getSubject());
    }

    private String encode(JwtClaimsSet claims) {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private String displayName(User user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        if (user.getUsername() != null) {
            return user.getUsername();
        }
        return String.valueOf(user.getId());
    }
}
