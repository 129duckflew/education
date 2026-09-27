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
@Table(name = "professor_review")
public class ProfessorReview extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int rating;

    private String content;
}
