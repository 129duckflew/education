package cn.duckflew.education.professor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorProfileRepository extends JpaRepository<ProfessorProfile, Long> {
    Page<ProfessorProfile> findByApproved(boolean approved, Pageable pageable);

    List<ProfessorProfile> findByApprovedTrue();
}
