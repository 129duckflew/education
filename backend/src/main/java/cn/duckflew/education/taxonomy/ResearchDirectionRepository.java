package cn.duckflew.education.taxonomy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResearchDirectionRepository extends JpaRepository<ResearchDirection, Long> {
    List<ResearchDirection> findByMajorId(Long majorId);
}
