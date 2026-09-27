package cn.duckflew.education.user;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "verification_code")
public class VerificationCode extends AuditableEntity {

    public enum Channel {EMAIL, SMS}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
