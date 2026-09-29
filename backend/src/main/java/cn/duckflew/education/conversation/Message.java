package cn.duckflew.education.conversation;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 私信消息。id 为权威顺序；file_id 承载图片/文件；client_msg_id 用于幂等。
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "message")
public class Message extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type = MessageType.TEXT;

    private String content;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "reply_to_id")
    private Long replyToId;

    @Column(name = "client_msg_id")
    private String clientMsgId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.NORMAL;
}
