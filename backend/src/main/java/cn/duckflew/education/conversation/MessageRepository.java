package cn.duckflew.education.conversation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByIdIn(Collection<Long> ids);

    List<Message> findByConversationIdAndIdLessThanOrderByIdDesc(Long conversationId, long beforeId, Pageable pageable);

    List<Message> findByConversationIdAndIdGreaterThanOrderByIdAsc(Long conversationId, long afterId, Pageable pageable);

    Optional<Message> findBySenderIdAndClientMsgId(Long senderId, String clientMsgId);
}
