package cn.duckflew.education.messaging;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    @Query("""
            select m from DirectMessage m
            where (m.fromUserId = :a and m.toUserId = :b) or (m.fromUserId = :b and m.toUserId = :a)
            order by m.createdAt desc
            """)
    Page<DirectMessage> findConversation(@Param("a") Long a, @Param("b") Long b, Pageable pageable);

    long countByToUserIdAndReadFalse(Long toUserId);

    @Query("""
            select case when m.fromUserId = :me then m.toUserId else m.fromUserId end
            from DirectMessage m
            where m.fromUserId = :me or m.toUserId = :me
            group by case when m.fromUserId = :me then m.toUserId else m.fromUserId end
            """)
    List<Long> findConversationPartners(@Param("me") Long me);

    @Modifying
    @Query("""
            update DirectMessage m set m.read = true
            where m.toUserId = :userId and m.fromUserId = :otherId and m.read = false
            """)
    int markConversationRead(@Param("userId") Long userId, @Param("otherId") Long otherId);
}
