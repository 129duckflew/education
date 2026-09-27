package cn.duckflew.education.guide;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuideAreaRepository extends JpaRepository<GuideArea, Long> {
    List<GuideArea> findByGuideId(Long guideId);

    List<GuideArea> findByAreaIdIn(List<Long> areaIds);

    void deleteByGuideId(Long guideId);
}
