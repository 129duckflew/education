package cn.duckflew.education.common.async;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/**
 * 异步任务 outbox：邮件/短信等成功后置任务，支持失败重试。
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "outbox_task")
public class OutboxTask extends AuditableEntity {

    public enum Status {PENDING, PROCESSING, DONE, FAILED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt = Instant.now();

    @Column(name = "completed_at")
    private Instant completedAt;
}
