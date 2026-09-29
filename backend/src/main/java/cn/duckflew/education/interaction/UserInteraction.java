package cn.duckflew.education.interaction;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户互动关系（恒 userAId &lt; userBId）。当前仅由「教授回答学生问题」写入，用于私信准入。
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_interaction")
public class UserInteraction extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_a_id", nullable = false)
    private Long userAId;

    @Column(name = "user_b_id", nullable = false)
    private Long userBId;
}
