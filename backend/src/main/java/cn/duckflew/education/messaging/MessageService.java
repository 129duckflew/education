package cn.duckflew.education.messaging;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final NotificationRepository notificationRepository;
    private final DirectMessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(NotificationRepository notificationRepository,
                          DirectMessageRepository messageRepository,
                          UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // ---------- 通知 ----------

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

    // ---------- 私信 ----------

    @Transactional(readOnly = true)
    public Page<MessageDtos.MessageView> conversation(Long userId, Long peerId, Pageable pageable) {
        Page<DirectMessage> page = messageRepository.findConversation(userId, peerId, pageable);
        List<MessageDtos.MessageView> views = page.getContent().stream().map(this::toView).toList();
        return new PageImpl<>(views, page.getPageable(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<MessageDtos.ConversationView> conversations(Long userId) {
        List<Long> partners = messageRepository.findConversationPartners(userId);
        if (partners.isEmpty()) {
            return List.of();
        }
        Map<Long, String> names = userRepository.findAllById(partners).stream()
                .collect(Collectors.toMap(User::getId, this::displayName));
        return partners.stream().map(peerId -> {
            var lastPage = messageRepository.findConversation(userId, peerId, PageRequest.of(0, 1));
            DirectMessage last = lastPage.getContent().isEmpty() ? null : lastPage.getContent().get(0);
            long unread = messageRepository.findConversation(userId, peerId, PageRequest.of(0, 200))
                    .getContent().stream()
                    .filter(m -> m.getToUserId().equals(userId) && !m.isRead()).count();
            return new MessageDtos.ConversationView(peerId, names.getOrDefault(peerId, "用户" + peerId),
                    last == null ? null : last.getContent(), last == null ? null : last.getCreatedAt(), unread);
        }).toList();
    }

    @Transactional
    public Long send(Long fromUserId, MessageDtos.SendMessageRequest request) {
        if (Objects.equals(fromUserId, request.toUserId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能给自己发消息");
        }
        if (!userRepository.existsById(request.toUserId())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        DirectMessage message = new DirectMessage();
        message.setFromUserId(fromUserId);
        message.setToUserId(request.toUserId());
        message.setContent(request.content());
        return messageRepository.save(message).getId();
    }

    @Transactional
    public void markConversationRead(Long userId, Long peerId) {
        messageRepository.markConversationRead(userId, peerId);
    }

    @Transactional(readOnly = true)
    public long unreadMessageCount(Long userId) {
        return messageRepository.countByToUserIdAndReadFalse(userId);
    }

    private MessageDtos.MessageView toView(DirectMessage m) {
        return new MessageDtos.MessageView(m.getId(), m.getFromUserId(), m.getToUserId(),
                m.getContent(), m.isRead(), m.getCreatedAt());
    }

    private String displayName(User user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        if (user.getRealName() != null && !user.getRealName().isBlank()) {
            return user.getRealName();
        }
        return user.getUsername() == null ? "用户" + user.getId() : user.getUsername();
    }
}
