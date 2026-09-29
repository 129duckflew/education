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
            NotificationAnchor anchorType,
            Long anchorId,
            Long anchorRefId,
            boolean read,
            Instant createdAt
    ) {
    }
}
