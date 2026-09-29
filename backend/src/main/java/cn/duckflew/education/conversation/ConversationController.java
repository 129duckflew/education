package cn.duckflew.education.conversation;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ApiResponse<List<ConversationDtos.ConversationView>> list() {
        return ApiResponse.ok(conversationService.list(CurrentUser.id()));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount() {
        return ApiResponse.ok(conversationService.unreadTotal(CurrentUser.id()));
    }

    /** 打开或创建 1:1 会话，返回会话 id。 */
    @PostMapping
    public ApiResponse<Long> open(@Valid @RequestBody ConversationDtos.OpenRequest request) {
        return ApiResponse.ok(conversationService.openOrCreate(CurrentUser.id(), request.peerId()));
    }

    @GetMapping("/{id}/messages")
    public ApiResponse<ConversationDtos.MessagePage> messages(
            @PathVariable Long id,
            @RequestParam(required = false) Long beforeId,
            @RequestParam(required = false) Long afterId,
            @RequestParam(defaultValue = "30") int size) {
        return ApiResponse.ok(conversationService.messages(CurrentUser.id(), id, beforeId, afterId, size));
    }

    @PostMapping("/{id}/messages")
    public ApiResponse<ConversationDtos.MessageView> send(
            @PathVariable Long id,
            @Valid @RequestBody ConversationDtos.SendRequest request) {
        return ApiResponse.ok(conversationService.send(CurrentUser.id(), id, request));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Void> read(@PathVariable Long id,
                                  @Valid @RequestBody ConversationDtos.ReadRequest request) {
        conversationService.read(CurrentUser.id(), id, request.upToMessageId());
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody ConversationDtos.UpdateRequest request) {
        conversationService.update(CurrentUser.id(), id, request);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/messages/{messageId}/recall")
    public ApiResponse<Void> recall(@PathVariable Long id, @PathVariable Long messageId) {
        conversationService.recall(CurrentUser.id(), id, messageId);
        return ApiResponse.ok();
    }
}
