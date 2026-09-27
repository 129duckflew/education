package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface QuestionLikeRepository extends JpaRepository<QuestionLike, Long> {

    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);

    long countByQuestionId(Long questionId);

    List<QuestionLike> findByQuestionIdIn(Collection<Long> questionIds);

    List<QuestionLike> findByUserId(Long userId);

    List<QuestionLike> findByUserIdAndQuestionIdIn(Long userId, Collection<Long> questionIds);

    void deleteByUserIdAndQuestionId(Long userId, Long questionId);

    @Query("""
            select new cn.duckflew.education.qa.IdCount(l.questionId, count(l))
            from QuestionLike l where l.questionId in :ids group by l.questionId
            """)
    List<IdCount> countByQuestionIds(@Param("ids") Collection<Long> ids);
}
