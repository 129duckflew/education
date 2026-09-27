package cn.duckflew.education.professor;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRankRepository extends JpaRepository<JobRank, Long> {
    boolean existsByName(String name);
}
