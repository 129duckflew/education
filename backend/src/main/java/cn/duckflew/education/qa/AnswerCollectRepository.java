package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AnswerCollectRepository extends JpaRepository<AnswerCollect, Long> {

    boolean existsByUserIdAndAnswerId(Long userId, Long answerId);

    long countByAnswerId(Long answerId);

    List<AnswerCollect> findByAnswerIdIn(Collection<Long> answerIds);

    List<AnswerCollect> findByUserIdAndAnswerIdIn(Long userId, Collection<Long> answerIds);

    List<AnswerCollect> findByUserId(Long userId);

    void deleteByUserIdAndAnswerId(Long userId, Long answerId);

    @Query("""
            select new cn.duckflew.education.qa.IdCount(c.answerId, count(c))
            from AnswerCollect c where c.answerId in :ids group by c.answerId
            """)
    List<IdCount> countByAnswerIds(@Param("ids") Collection<Long> ids);
}
