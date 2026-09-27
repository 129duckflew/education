package cn.duckflew.education.professor;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "professor_area")
public class ProfessorArea extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Column(name = "area_id", nullable = false)
    private Long areaId;
}
