package cn.duckflew.education.professor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProfessorReviewRepository extends JpaRepository<ProfessorReview, Long> {

    List<ProfessorReview> findByProfessorIdOrderByCreatedAtDesc(Long professorId);

    long countByProfessorId(Long professorId);

    @Query("""
            select new cn.duckflew.education.professor.RatingStat(r.professorId, avg(r.rating), count(r))
            from ProfessorReview r where r.professorId in :ids group by r.professorId
            """)
    List<RatingStat> statsByProfessorIds(@Param("ids") Collection<Long> ids);
}
