package cn.duckflew.education.messaging;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.security.CurrentUser;
import cn.duckflew.education.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;
    private final JwtService jwtService;
    private final SseEmitterRegistry sseEmitterRegistry;

    public MessageController(MessageService messageService,
                             JwtService jwtService,
                             SseEmitterRegistry sseEmitterRegistry) {
        this.messageService = messageService;
        this.jwtService = jwtService;
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    /**
     * 通知实时推送（SSE）。EventSource 无法自定义请求头，故用 query 参数传 token。
     */
    @GetMapping(value = "/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam("token") String token) {
        Long userId;
        try {
            userId = jwtService.userIdFromAccessToken(token);
        } catch (RuntimeException ex) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return sseEmitterRegistry.register(userId);
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
