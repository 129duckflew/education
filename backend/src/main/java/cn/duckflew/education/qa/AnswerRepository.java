package cn.duckflew.education.qa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestionIdOrderByCreatedAtAsc(Long questionId);

    List<Answer> findByQuestionIdIn(Collection<Long> questionIds);

    long countByQuestionId(Long questionId);

    long countByProfessorId(Long professorId);

    Optional<Answer> findByQuestionIdAndProfessorId(Long questionId, Long professorId);

    List<Answer> findByProfessorId(Long professorId);

    @Query("""
            select new cn.duckflew.education.qa.IdCount(a.questionId, count(a))
            from Answer a where a.questionId in :ids group by a.questionId
            """)
    List<IdCount> countByQuestionIds(@Param("ids") Collection<Long> ids);
}
