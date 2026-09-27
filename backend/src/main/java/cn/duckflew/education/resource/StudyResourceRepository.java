package cn.duckflew.education.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyResourceRepository extends JpaRepository<StudyResource, Long> {
    Page<StudyResource> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<StudyResource> findByProfessorId(Long professorId, Pageable pageable);
}
