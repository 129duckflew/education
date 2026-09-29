package cn.duckflew.education.conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByPairKey(String pairKey);

    @Modifying
    @Query(value = "insert into conversation (type, pair_key) values ('SINGLE', :pairKey) "
            + "on conflict (pair_key) do nothing", nativeQuery = true)
    void insertSingleIfAbsent(@Param("pairKey") String pairKey);
}
