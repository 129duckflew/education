package cn.duckflew.education.conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {

    List<ConversationMember> findByConversationId(Long conversationId);

    List<ConversationMember> findByConversationIdIn(Collection<Long> conversationIds);

    Optional<ConversationMember> findByConversationIdAndUserId(Long conversationId, Long userId);

    List<ConversationMember> findByUserIdAndHiddenFalse(Long userId);

    @Query("""
            select coalesce(sum(m.unreadCount), 0) from ConversationMember m
            where m.userId = :userId and m.hidden = false
            """)
    long sumUnread(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update ConversationMember m set m.unreadCount = m.unreadCount + 1
            where m.conversationId = :conversationId and m.userId <> :senderId
            """)
    int incrementUnread(@Param("conversationId") Long conversationId, @Param("senderId") Long senderId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update ConversationMember m set m.unreadCount = 0, m.lastReadMessageId = :upToMessageId
            where m.conversationId = :conversationId and m.userId = :userId
            """)
    int markRead(@Param("conversationId") Long conversationId,
                 @Param("userId") Long userId,
                 @Param("upToMessageId") long upToMessageId);
}
