package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestionIdOrderByCreatedAtAsc(Long questionId);

    List<Answer> findByQuestionIdIn(Collection<Long> questionIds);

    long countByQuestionId(Long questionId);

    long countByProfessorId(Long professorId);

    java.util.Optional<Answer> findByQuestionIdAndProfessorId(Long questionId, Long professorId);
}
