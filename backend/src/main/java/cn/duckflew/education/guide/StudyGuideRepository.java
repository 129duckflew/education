package cn.duckflew.education.guide;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyGuideRepository extends JpaRepository<StudyGuide, Long> {
    List<StudyGuide> findAllByOrderBySortOrderAscIdAsc();

    List<StudyGuide> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<StudyGuide> findByNameContainingIgnoreCaseOrderByIdAsc(String name);

    void deleteByParentId(Long parentId);
}
