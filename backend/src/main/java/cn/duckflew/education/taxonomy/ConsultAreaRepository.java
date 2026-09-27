package cn.duckflew.education.taxonomy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultAreaRepository extends JpaRepository<ConsultArea, Long> {
    List<ConsultArea> findAllByOrderBySortOrderAscIdAsc();

    List<ConsultArea> findByParentIdOrderBySortOrderAsc(Long parentId);
}
