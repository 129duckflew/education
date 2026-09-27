package cn.duckflew.education.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class MessageDtos {

    private MessageDtos() {
    }

    public record NotificationView(
            Long id,
            Long fromUserId,
            NotificationType type,
            Long resourceId,
            Long relatedUserId,
            boolean read,
            Instant createdAt
    ) {
    }

    public record MessageView(
            Long id,
            Long fromUserId,
            Long toUserId,
            String content,
            boolean read,
            Instant createdAt
    ) {
    }

    public record ConversationView(
            Long peerId,
            String peerName,
            String lastMessage,
            Instant lastAt,
            long unread
    ) {
    }

    public record SendMessageRequest(
            @NotNull(message = "接收方不能为空") Long toUserId,
            @NotBlank(message = "消息内容不能为空") @Size(max = 2000) String content
    ) {
    }
}
