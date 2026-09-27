package cn.duckflew.education.qa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SubmitQuestionRequest(
        @NotBlank(message = "标题不能为空") @Size(max = 255) String title,
        @NotBlank(message = "问题描述不能为空") String description,
        @NotEmpty(message = "至少选择一个领域") List<Long> areaIds,
        @NotEmpty(message = "至少选择一位教授") List<Long> professorIds,
        List<Long> imageFileIds
) {
}
