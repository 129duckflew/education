package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionProfessorRepository extends JpaRepository<QuestionProfessor, Long> {
    List<QuestionProfessor> findByQuestionId(Long questionId);

    List<QuestionProfessor> findByProfessorId(Long professorId);
}
