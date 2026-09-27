package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AnswerLikeRepository extends JpaRepository<AnswerLike, Long> {

    boolean existsByUserIdAndAnswerId(Long userId, Long answerId);

    long countByAnswerId(Long answerId);

    List<AnswerLike> findByAnswerIdIn(Collection<Long> answerIds);

    List<AnswerLike> findByUserIdAndAnswerIdIn(Long userId, Collection<Long> answerIds);

    void deleteByUserIdAndAnswerId(Long userId, Long answerId);

    @Query("""
            select new cn.duckflew.education.qa.IdCount(l.answerId, count(l))
            from AnswerLike l where l.answerId in :ids group by l.answerId
            """)
    List<IdCount> countByAnswerIds(@Param("ids") Collection<Long> ids);
}
