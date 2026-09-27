package cn.duckflew.education.messaging;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import cn.duckflew.education.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/notifications")
    public ApiResponse<PageResponse<MessageDtos.NotificationView>> notifications(
            @RequestParam(required = false) NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok(PageResponse.of(
                messageService.notifications(CurrentUser.id(), type, pageable)));
    }

    @GetMapping("/notifications/unread-count")
    public ApiResponse<Long> unreadCount() {
        return ApiResponse.ok(messageService.unreadNotificationCount(CurrentUser.id()));
    }

    @GetMapping("/notifications/unread-by-type")
    public ApiResponse<Map<String, Long>> unreadByType() {
        return ApiResponse.ok(messageService.unreadByType(CurrentUser.id()));
    }

    @PostMapping("/notifications/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        messageService.markNotificationRead(CurrentUser.id(), id);
        return ApiResponse.ok();
    }

    @PostMapping("/notifications/read-all")
    public ApiResponse<Void> markAllRead() {
        messageService.markAllNotificationsRead(CurrentUser.id());
        return ApiResponse.ok();
    }

    @GetMapping("/messages/conversations")
    public ApiResponse<List<MessageDtos.ConversationView>> conversations() {
        return ApiResponse.ok(messageService.conversations(CurrentUser.id()));
    }

    @GetMapping("/messages/with/{peerId}")
    public ApiResponse<PageResponse<MessageDtos.MessageView>> conversation(
            @PathVariable Long peerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok(PageResponse.of(
                messageService.conversation(CurrentUser.id(), peerId, pageable)));
    }

    @PostMapping("/messages")
    public ApiResponse<Long> send(@Valid @RequestBody MessageDtos.SendMessageRequest request) {
        return ApiResponse.ok(messageService.send(CurrentUser.id(), request));
    }

    @PostMapping("/messages/with/{peerId}/read")
    public ApiResponse<Void> markConversationRead(@PathVariable Long peerId) {
        messageService.markConversationRead(CurrentUser.id(), peerId);
        return ApiResponse.ok();
    }

    @GetMapping("/messages/unread-count")
    public ApiResponse<Long> unreadMessageCount() {
        return ApiResponse.ok(messageService.unreadMessageCount(CurrentUser.id()));
    }
}
