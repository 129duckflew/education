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
@Table(name = "question")
public class Question extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status = QuestionStatus.AUDITING;

    @Column(name = "order_id")
    private Long orderId;
}
