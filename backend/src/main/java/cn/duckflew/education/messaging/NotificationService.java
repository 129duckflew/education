package cn.duckflew.education.messaging;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void notify(Long toUserId, Long fromUserId, NotificationType type,
                       Long resourceId, Long relatedUserId) {
        if (toUserId == null || toUserId.equals(fromUserId)) {
            return;
        }
        repository.save(Notification.of(toUserId, fromUserId, type, resourceId, relatedUserId));
    }
}
