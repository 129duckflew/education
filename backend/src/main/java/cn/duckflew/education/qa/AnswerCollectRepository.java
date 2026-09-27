package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AnswerCollectRepository extends JpaRepository<AnswerCollect, Long> {

    boolean existsByUserIdAndAnswerId(Long userId, Long answerId);

    long countByAnswerId(Long answerId);

    List<AnswerCollect> findByAnswerIdIn(Collection<Long> answerIds);

    List<AnswerCollect> findByUserId(Long userId);

    void deleteByUserIdAndAnswerId(Long userId, Long answerId);
}
