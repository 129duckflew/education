package cn.duckflew.education.professor;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DegreeRepository extends JpaRepository<Degree, Long> {
    boolean existsByName(String name);
}
