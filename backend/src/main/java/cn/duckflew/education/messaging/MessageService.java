package cn.duckflew.education.messaging;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    private final NotificationRepository notificationRepository;

    public MessageService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public Page<MessageDtos.NotificationView> notifications(Long userId, NotificationType type, Pageable pageable) {
        Page<Notification> page = type == null
                ? notificationRepository.findByToUserIdOrderByCreatedAtDesc(userId, pageable)
                : notificationRepository.findByToUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        List<MessageDtos.NotificationView> views = page.getContent().stream()
                .map(n -> new MessageDtos.NotificationView(n.getId(), n.getFromUserId(), n.getType(),
                        n.getResourceId(), n.getRelatedUserId(), n.isRead(), n.getCreatedAt()))
                .toList();
        return new PageImpl<>(views, page.getPageable(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public long unreadNotificationCount(Long userId) {
        return notificationRepository.countByToUserIdAndReadFalse(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> unreadByType(Long userId) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (NotificationType type : NotificationType.values()) {
            result.put(type.name(), notificationRepository.countByToUserIdAndTypeAndReadFalse(userId, type));
        }
        return result;
    }

    @Transactional
    public void markNotificationRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndToUserId(notificationId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "通知不存在"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllNotificationsRead(Long userId) {
        notificationRepository.markAllRead(userId);
    }
}
