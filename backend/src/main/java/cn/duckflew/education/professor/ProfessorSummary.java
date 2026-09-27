package cn.duckflew.education.professor;

import java.math.BigDecimal;
import java.util.List;

public record ProfessorSummary(
        Long userId,
        String realName,
        Long avatarFileId,
        String jobRankName,
        String introduction,
        BigDecimal consultPrice,
        List<String> areaNames,
        double rating,
        long reviewCount
) {
}
