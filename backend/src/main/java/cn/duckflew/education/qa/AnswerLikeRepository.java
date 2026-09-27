package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AnswerLikeRepository extends JpaRepository<AnswerLike, Long> {

    boolean existsByUserIdAndAnswerId(Long userId, Long answerId);

    long countByAnswerId(Long answerId);

    List<AnswerLike> findByAnswerIdIn(Collection<Long> answerIds);

    void deleteByUserIdAndAnswerId(Long userId, Long answerId);
}
