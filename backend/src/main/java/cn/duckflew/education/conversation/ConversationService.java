package cn.duckflew.education.conversation;

import cn.duckflew.education.common.event.AfterCommit;
import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.file.FileInfo;
import cn.duckflew.education.file.FileObject;
import cn.duckflew.education.file.FileObjectRepository;
import cn.duckflew.education.file.StorageProperties;
import cn.duckflew.education.interaction.InteractionService;
import cn.duckflew.education.messaging.SseEmitterRegistry;
import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private static final int MAX_PAGE = 100;
    private static final int DEFAULT_PAGE = 30;
    private static final long RECALL_WINDOW_SECONDS = 120L;

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FileObjectRepository fileObjectRepository;
    private final StorageProperties storageProperties;
    private final InteractionService interactionService;
    private final SseEmitterRegistry sseEmitterRegistry;

    public ConversationService(ConversationRepository conversationRepository,
                               ConversationMemberRepository memberRepository,
                               MessageRepository messageRepository,
                               UserRepository userRepository,
                               FileObjectRepository fileObjectRepository,
                               StorageProperties storageProperties,
                               InteractionService interactionService,
                               SseEmitterRegistry sseEmitterRegistry) {
        this.conversationRepository = conversationRepository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.fileObjectRepository = fileObjectRepository;
        this.storageProperties = storageProperties;
        this.interactionService = interactionService;
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    // ---------- 会话 ----------

    /**
     * 打开或创建 1:1 会话。仅当双方存在互动关系（教授回答过学生问题）时允许。
     */
    @Transactional
    public Long openOrCreate(Long me, Long peerId) {
        if (Objects.equals(me, peerId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能与自己会话");
        }
        if (!userRepository.existsById(peerId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!interactionService.canMessage(me, peerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "需先与该用户有过问答互动才能私信");
        }
        String pairKey = pairKey(me, peerId);
        Conversation conversation = conversationRepository.findByPairKey(pairKey)
                .orElseGet(() -> {
                    conversationRepository.insertSingleIfAbsent(pairKey);
                    return conversationRepository.findByPairKey(pairKey)
                            .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "创建会话失败"));
                });
        ensureMember(conversation.getId(), me);
        ensureMember(conversation.getId(), peerId);
        return conversation.getId();
    }

    @Transactional(readOnly = true)
    public List<ConversationDtos.ConversationView> list(Long me) {
        List<ConversationMember> memberships = memberRepository.findByUserIdAndHiddenFalse(me);
        if (memberships.isEmpty()) {
            return List.of();
        }
        List<Long> conversationIds = memberships.stream()
                .map(ConversationMember::getConversationId).toList();
        Map<Long, Conversation> conversations = conversationRepository.findAllById(conversationIds).stream()
                .collect(Collectors.toMap(Conversation::getId, c -> c));

        Map<Long, List<ConversationMember>> membersByConversation = memberRepository
                .findByConversationIdIn(conversationIds).stream()
                .collect(Collectors.groupingBy(ConversationMember::getConversationId));

        Set<Long> peerIds = new HashSet<>();
        Map<Long, Long> peerOf = new HashMap<>();
        membersByConversation.forEach((conversationId, members) -> members.stream()
                .map(ConversationMember::getUserId)
                .filter(userId -> !userId.equals(me))
                .findFirst()
                .ifPresent(peerId -> {
                    peerOf.put(conversationId, peerId);
                    peerIds.add(peerId);
                }));
        Map<Long, User> peers = userRepository.findAllById(peerIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Set<Long> lastMessageIds = conversations.values().stream()
                .map(Conversation::getLastMessageId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Message> lastMessages = lastMessageIds.isEmpty() ? Map.of()
                : messageRepository.findByIdIn(lastMessageIds).stream()
                .collect(Collectors.toMap(Message::getId, m -> m));

        Map<Long, ConversationMember> myMemberships = memberships.stream()
                .collect(Collectors.toMap(ConversationMember::getConversationId, m -> m, (a, b) -> a));

        List<ConversationDtos.ConversationView> views = new ArrayList<>();
        for (Conversation conversation : conversations.values()) {
            ConversationMember membership = myMemberships.get(conversation.getId());
            if (membership == null) {
                continue;
            }
            Long peerId = peerOf.get(conversation.getId());
            User peer = peerId == null ? null : peers.get(peerId);
            Message last = conversation.getLastMessageId() == null
                    ? null : lastMessages.get(conversation.getLastMessageId());
            views.add(new ConversationDtos.ConversationView(
                    conversation.getId(), conversation.getType(), peerId,
                    peer == null ? "用户" : displayName(peer), conversation.getTitle(),
                    last == null ? null : lastSummary(last),
                    last == null ? null : last.getType(),
                    conversation.getLastMessageAt(), membership.getUnreadCount(),
                    membership.isPinned(), membership.isMuted()));
        }
        views.sort(Comparator
                .comparing(ConversationDtos.ConversationView::pinned).reversed()
                .thenComparing(ConversationDtos.ConversationView::lastAt,
                        Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(ConversationDtos.ConversationView::id, Comparator.reverseOrder()));
        return views;
    }

    @Transactional(readOnly = true)
    public long unreadTotal(Long me) {
        return memberRepository.sumUnread(me);
    }

    @Transactional
    public void update(Long me, Long conversationId, ConversationDtos.UpdateRequest request) {
        ConversationMember member = requireMember(me, conversationId);
        if (request.muted() != null) {
            member.setMuted(request.muted());
        }
        if (request.pinned() != null) {
            member.setPinned(request.pinned());
        }
        if (request.hidden() != null) {
            member.setHidden(request.hidden());
        }
        memberRepository.save(member);
    }

    // ---------- 消息 ----------

    @Transactional(readOnly = true)
    public ConversationDtos.MessagePage messages(Long me, Long conversationId,
                                                 Long beforeId, Long afterId, int size) {
        requireMember(me, conversationId);
        int limit = size <= 0 ? DEFAULT_PAGE : Math.min(size, MAX_PAGE);

        if (afterId != null) {
            List<Message> rows = messageRepository
                    .findByConversationIdAndIdGreaterThanOrderByIdAsc(conversationId, afterId,
                            PageRequest.of(0, limit));
            return new ConversationDtos.MessagePage(assemble(rows), false);
        }

        long before = beforeId == null ? Long.MAX_VALUE : beforeId;
        List<Message> rows = new ArrayList<>(messageRepository
                .findByConversationIdAndIdLessThanOrderByIdDesc(conversationId, before,
                        PageRequest.of(0, limit + 1)));
        boolean hasMore = rows.size() > limit;
        if (hasMore) {
            rows = rows.subList(0, limit);
        }
        Collections.reverse(rows);
        return new ConversationDtos.MessagePage(assemble(rows), hasMore);
    }

    @Transactional
    public ConversationDtos.MessageView send(Long me, Long conversationId,
                                             ConversationDtos.SendRequest request) {
        ConversationMember membership = requireMember(me, conversationId);
        membership.setHidden(false);

        MessageType type = request.type() == null ? MessageType.TEXT : request.type();
        validateContent(type, request);
        if (request.fileId() != null && !fileObjectRepository.existsById(request.fileId())) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        if (request.clientMsgId() != null) {
            Optional<Message> existed = messageRepository
                    .findBySenderIdAndClientMsgId(me, request.clientMsgId());
            if (existed.isPresent()) {
                return assemble(List.of(existed.get())).get(0);
            }
        }

        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(me);
        message.setType(type);
        message.setContent(request.content());
        message.setFileId(request.fileId());
        message.setReplyToId(request.replyToId());
        message.setClientMsgId(request.clientMsgId());
        message.setStatus(MessageStatus.NORMAL);
        messageRepository.save(message);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "会话不存在"));
        conversation.setLastMessageId(message.getId());
        conversation.setLastMessageAt(message.getCreatedAt() == null ? Instant.now() : message.getCreatedAt());
        conversationRepository.save(conversation);
        memberRepository.incrementUnread(conversationId, me);

        List<ConversationDtos.MessageView> views = assemble(List.of(message));
        ConversationDtos.MessageView view = views.get(0);
        List<Long> memberIds = memberRepository.findByConversationId(conversationId).stream()
                .map(ConversationMember::getUserId).toList();
        AfterCommit.run(() -> memberIds.forEach(userId ->
                sseEmitterRegistry.publish(userId, "message", view)));
        return view;
    }

    @Transactional
    public void read(Long me, Long conversationId, Long upToMessageId) {
        requireMember(me, conversationId);
        long cursor = upToMessageId == null ? 0L : upToMessageId;
        memberRepository.markRead(conversationId, me, cursor);
        List<Long> memberIds = memberRepository.findByConversationId(conversationId).stream()
                .map(ConversationMember::getUserId).toList();
        var event = new ConversationDtos.ReadEvent(conversationId, me, cursor);
        AfterCommit.run(() -> memberIds.forEach(userId ->
                sseEmitterRegistry.publish(userId, "read", event)));
    }

    @Transactional
    public void recall(Long me, Long conversationId, Long messageId) {
        requireMember(me, conversationId);
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "消息不存在"));
        if (!message.getConversationId().equals(conversationId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "消息不属于该会话");
        }
        if (!message.getSenderId().equals(me)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能撤回自己的消息");
        }
        if (message.getStatus() == MessageStatus.RECALLED) {
            return;
        }
        Instant created = message.getCreatedAt();
        if (created != null && created.isBefore(Instant.now().minusSeconds(RECALL_WINDOW_SECONDS))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "超过撤回时限");
        }
        message.setStatus(MessageStatus.RECALLED);
        message.setContent(null);
        message.setFileId(null);
        messageRepository.save(message);

        ConversationDtos.MessageView view = assemble(List.of(message)).get(0);
        List<Long> memberIds = memberRepository.findByConversationId(conversationId).stream()
                .map(ConversationMember::getUserId).toList();
        AfterCommit.run(() -> memberIds.forEach(userId ->
                sseEmitterRegistry.publish(userId, "message", view)));
    }

    // ---------- 内部 ----------

    private void validateContent(MessageType type, ConversationDtos.SendRequest request) {
        if (type == MessageType.TEXT) {
            if (request.content() == null || request.content().isBlank()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "消息内容不能为空");
            }
        } else if (type == MessageType.IMAGE || type == MessageType.FILE) {
            if (request.fileId() == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "图片/文件消息缺少 fileId");
            }
        }
    }

    private ConversationMember requireMember(Long userId, Long conversationId) {
        return memberRepository.findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN, "不是该会话成员"));
    }

    private void ensureMember(Long conversationId, Long userId) {
        if (memberRepository.findByConversationIdAndUserId(conversationId, userId).isEmpty()) {
            memberRepository.save(ConversationMember.of(conversationId, userId));
        }
    }

    private List<ConversationDtos.MessageView> assemble(List<Message> messages) {
        if (messages.isEmpty()) {
            return List.of();
        }
        Set<Long> senderIds = messages.stream().map(Message::getSenderId).collect(Collectors.toSet());
        Map<Long, User> senders = userRepository.findAllById(senderIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        Set<Long> fileIds = messages.stream().map(Message::getFileId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, FileObject> files = fileIds.isEmpty() ? Map.of()
                : fileObjectRepository.findAllById(fileIds).stream()
                .collect(Collectors.toMap(FileObject::getId, f -> f));

        return messages.stream().map(message -> {
            User sender = senders.get(message.getSenderId());
            FileObject file = message.getFileId() == null ? null : files.get(message.getFileId());
            FileInfo fileInfo = file == null ? null
                    : FileInfo.from(file, storageProperties.publicBaseUrl());
            return new ConversationDtos.MessageView(
                    message.getId(), message.getConversationId(), message.getSenderId(),
                    sender == null ? "用户" : displayName(sender), message.getType(),
                    message.getContent(), fileInfo, message.getReplyToId(),
                    message.getClientMsgId(), message.getStatus(), message.getCreatedAt());
        }).toList();
    }

    private String lastSummary(Message message) {
        if (message.getStatus() == MessageStatus.RECALLED) {
            return "[已撤回]";
        }
        return switch (message.getType()) {
            case IMAGE -> "[图片]";
            case FILE -> "[文件]";
            default -> message.getContent();
        };
    }

    private String pairKey(Long a, Long b) {
        return Math.min(a, b) + ":" + Math.max(a, b);
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
