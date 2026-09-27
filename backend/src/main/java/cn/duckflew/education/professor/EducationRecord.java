package cn.duckflew.education.professor;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "education_record")
public class EducationRecord extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(name = "major_name")
    private String majorName;

    @Column(name = "degree_id")
    private Long degreeId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "full_time", nullable = false)
    private boolean fullTime = true;

    @Column(name = "research_direction")
    private String researchDirection;
}
