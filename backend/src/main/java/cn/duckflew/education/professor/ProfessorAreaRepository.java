package cn.duckflew.education.professor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorAreaRepository extends JpaRepository<ProfessorArea, Long> {
    List<ProfessorArea> findByProfessorId(Long professorId);

    List<ProfessorArea> findByAreaId(Long areaId);

    void deleteByProfessorId(Long professorId);
}
