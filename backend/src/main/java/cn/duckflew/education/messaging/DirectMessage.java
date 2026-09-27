package cn.duckflew.education.messaging;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "direct_message")
public class DirectMessage extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean read;
}
