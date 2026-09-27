package cn.duckflew.education.taxonomy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaxonomyRequests {

    private TaxonomyRequests() {
    }

    public record UniversityRequest(
            @NotBlank(message = "学校名称不能为空") @Size(max = 200) String name,
            String province,
            String city,
            String level,
            String department
    ) {
    }

    public record MajorRequest(
            String code,
            @NotBlank(message = "专业名称不能为空") @Size(max = 200) String name,
            Long parentId
    ) {
    }

    public record ResearchDirectionRequest(
            @NotBlank(message = "研究方向名称不能为空") @Size(max = 200) String name,
            Long majorId
    ) {
    }
}
