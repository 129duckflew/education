package cn.duckflew.education.messaging;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final SseEmitterRegistry sseEmitterRegistry;

    public NotificationService(NotificationRepository repository, SseEmitterRegistry sseEmitterRegistry) {
        this.repository = repository;
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @Transactional
    public void notify(Long toUserId, Long fromUserId, NotificationType type,
                       Long resourceId, Long relatedUserId) {
        if (toUserId == null || toUserId.equals(fromUserId)) {
            return;
        }
        Notification saved = repository.save(
                Notification.of(toUserId, fromUserId, type, resourceId, relatedUserId));
        sseEmitterRegistry.publish(toUserId, "notification",
                new MessageDtos.NotificationView(saved.getId(), saved.getFromUserId(), saved.getType(),
                        saved.getResourceId(), saved.getRelatedUserId(), saved.isRead(), saved.getCreatedAt()));
    }
}
