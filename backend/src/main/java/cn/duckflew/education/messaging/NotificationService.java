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
        notify(toUserId, fromUserId, type, resourceId, relatedUserId, null, null, null);
    }

    /**
     * @param resourceId  统一为问题 id，前端据此打开问题详情
     * @param anchorType  深链目标类型（回答 / 问题评论 / 回答评论）
     * @param anchorId    对应回答 id 或评论 id
     * @param anchorRefId 回答评论时所属回答 id
     */
    @Transactional
    public void notify(Long toUserId, Long fromUserId, NotificationType type,
                       Long resourceId, Long relatedUserId,
                       NotificationAnchor anchorType, Long anchorId, Long anchorRefId) {
        if (toUserId == null || toUserId.equals(fromUserId)) {
            return;
        }
        Notification saved = repository.save(
                Notification.of(toUserId, fromUserId, type, resourceId, relatedUserId,
                        anchorType, anchorId, anchorRefId));
        sseEmitterRegistry.publish(toUserId, "notification",
                new MessageDtos.NotificationView(saved.getId(), saved.getFromUserId(), saved.getType(),
                        saved.getResourceId(), saved.getRelatedUserId(),
                        saved.getAnchorType(), saved.getAnchorId(), saved.getAnchorRefId(),
                        saved.isRead(), saved.getCreatedAt()));
    }
}
