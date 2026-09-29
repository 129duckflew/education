package cn.duckflew.education.conversation;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "conversation_member")
public class ConversationMember extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.MEMBER;

    @Column(name = "last_read_message_id", nullable = false)
    private long lastReadMessageId;

    @Column(name = "unread_count", nullable = false)
    private int unreadCount;

    @Column(nullable = false)
    private boolean muted;

    @Column(nullable = false)
    private boolean pinned;

    @Column(nullable = false)
    private boolean hidden;

    public static ConversationMember of(Long conversationId, Long userId) {
        ConversationMember member = new ConversationMember();
        member.setConversationId(conversationId);
        member.setUserId(userId);
        return member;
    }
}
