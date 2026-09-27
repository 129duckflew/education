package cn.duckflew.education.professor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教授本人视角的资料（含未通过状态）。
 */
public record ProfessorSelfView(
        Long userId,
        boolean approved,
        Long jobRankId,
        String introduction,
        BigDecimal consultPrice,
        Long cvFileId,
        List<Long> areaIds,
        List<ProfessorDetail.EducationView> educations
) {
}
