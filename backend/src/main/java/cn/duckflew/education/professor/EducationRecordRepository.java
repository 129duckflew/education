package cn.duckflew.education.professor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRecordRepository extends JpaRepository<EducationRecord, Long> {
    List<EducationRecord> findByProfessorIdOrderByStartDateAsc(Long professorId);

    void deleteByProfessorId(Long professorId);
}
