package cn.duckflew.education.conversation;

import cn.duckflew.education.file.FileInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public class ConversationDtos {

    private ConversationDtos() {
    }

    public record OpenRequest(@NotNull(message = "会话对象不能为空") Long peerId) {
    }

    public record SendRequest(
            @Size(max = 4000) String content,
            MessageType type,
            Long fileId,
            Long replyToId,
            @Size(max = 64) String clientMsgId
    ) {
    }

    public record ReadRequest(@NotNull(message = "已读位置不能为空") Long upToMessageId) {
    }

    public record UpdateRequest(Boolean muted, Boolean pinned, Boolean hidden) {
    }

    public record MessageView(
            Long id,
            Long conversationId,
            Long senderId,
            String senderName,
            MessageType type,
            String content,
            FileInfo file,
            Long replyToId,
            String clientMsgId,
            MessageStatus status,
            Instant createdAt
    ) {
    }

    /** list 按 id 升序（便于直接渲染），hasMore 表示上方还有更早的历史。 */
    public record MessagePage(List<MessageView> list, boolean hasMore) {
    }

    public record ConversationView(
            Long id,
            ConversationType type,
            Long peerId,
            String peerName,
            String title,
            String lastMessage,
            MessageType lastMessageType,
            Instant lastAt,
            long unread,
            boolean pinned,
            boolean muted
    ) {
    }

    public record ReadEvent(Long conversationId, Long readerId, Long upToMessageId) {
    }
}
