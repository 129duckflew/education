package cn.duckflew.education.conversation;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * 会话。1:1 用 pair_key（least:greatest 规范化）保证唯一；GROUP 为后续预留。
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "conversation")
public class Conversation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationType type = ConversationType.SINGLE;

    @Column(name = "pair_key")
    private String pairKey;

    private String title;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "last_message_id")
    private Long lastMessageId;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;
}
