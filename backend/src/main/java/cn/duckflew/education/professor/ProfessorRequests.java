package cn.duckflew.education.professor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class ProfessorRequests {

    private ProfessorRequests() {
    }

    public record ApplyRequest(
            Long jobRankId,
            @Size(max = 5000) String introduction,
            BigDecimal consultPrice,
            Long cvFileId
    ) {
    }

    public record UpdateRequest(
            Long jobRankId,
            @Size(max = 5000) String introduction,
            BigDecimal consultPrice,
            Long cvFileId
    ) {
    }

    public record AreasRequest(@NotNull List<Long> areaIds) {
    }

    public record ReviewRequest(
            @NotNull @Min(0) @Max(10) Integer rating,
            @Size(max = 1000) String content
    ) {
    }

    public record EducationRequest(
            @NotBlank @Size(max = 200) String schoolName,
            @Size(max = 200) String majorName,
            Long degreeId,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            Boolean fullTime,
            @Size(max = 200) String researchDirection
    ) {
    }
}
