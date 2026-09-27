package cn.duckflew.education.professor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorReviewRepository extends JpaRepository<ProfessorReview, Long> {
    Page<ProfessorReview> findByProfessorId(Long professorId, Pageable pageable);

    List<ProfessorReview> findByProfessorId(Long professorId);
}
