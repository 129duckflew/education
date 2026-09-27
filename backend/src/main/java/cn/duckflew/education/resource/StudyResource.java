package cn.duckflew.education.resource;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "study_resource")
public class StudyResource extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Column(nullable = false)
    private String name;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    private String remark;
}
