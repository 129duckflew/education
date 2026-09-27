package cn.duckflew.education.professor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ProfessorDetail(
        ProfessorSummary summary,
        List<EducationView> educations,
        List<ReviewView> reviews
) {
    public record EducationView(
            Long id,
            String schoolName,
            String majorName,
            String degreeName,
            LocalDate startDate,
            LocalDate endDate,
            boolean fullTime,
            String researchDirection
    ) {
    }

    public record ReviewView(
            Long id,
            Long userId,
            String userName,
            int rating,
            String content,
            Instant createdAt
    ) {
    }
}
