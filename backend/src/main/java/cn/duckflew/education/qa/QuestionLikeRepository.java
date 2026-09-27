package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface QuestionLikeRepository extends JpaRepository<QuestionLike, Long> {

    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);

    long countByQuestionId(Long questionId);

    List<QuestionLike> findByQuestionIdIn(Collection<Long> questionIds);

    List<QuestionLike> findByUserId(Long userId);
}
