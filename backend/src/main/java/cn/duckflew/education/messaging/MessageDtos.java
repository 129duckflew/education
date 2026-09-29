package cn.duckflew.education.messaging;

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
}
