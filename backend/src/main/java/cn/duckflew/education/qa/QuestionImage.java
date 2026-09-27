package cn.duckflew.education.qa;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "question_image")
public class QuestionImage extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
