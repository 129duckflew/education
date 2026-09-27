package cn.duckflew.education.taxonomy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UniversityMajorRepository extends JpaRepository<UniversityMajor, Long> {
    List<UniversityMajor> findByParentId(Long parentId);
}
