package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface QuestionAreaRepository extends JpaRepository<QuestionArea, Long> {
    List<QuestionArea> findByQuestionId(Long questionId);

    List<QuestionArea> findByQuestionIdIn(Collection<Long> questionIds);

    void deleteByQuestionId(Long questionId);
}
