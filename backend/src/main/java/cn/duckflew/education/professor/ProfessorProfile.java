package cn.duckflew.education.professor;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "professor_profile")
public class ProfessorProfile extends AuditableEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "job_rank_id")
    private Long jobRankId;

    private String introduction;

    @Column(name = "consult_price", nullable = false)
    private BigDecimal consultPrice = BigDecimal.ZERO;

    @Column(name = "cv_file_id")
    private Long cvFileId;

    @Column(nullable = false)
    private boolean approved;
}
