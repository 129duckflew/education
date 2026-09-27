package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface QuestionImageRepository extends JpaRepository<QuestionImage, Long> {
    List<QuestionImage> findByQuestionIdOrderBySortOrderAsc(Long questionId);

    List<QuestionImage> findByQuestionIdIn(Collection<Long> questionIds);

    void deleteByQuestionId(Long questionId);
}
