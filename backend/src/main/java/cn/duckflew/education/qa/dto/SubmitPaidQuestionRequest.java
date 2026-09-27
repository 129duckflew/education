package cn.duckflew.education.qa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SubmitPaidQuestionRequest(
        @NotBlank(message = "标题不能为空") @Size(max = 255) String title,
        @NotBlank(message = "问题描述不能为空") String description,
        @NotEmpty(message = "至少选择一个领域") List<Long> areaIds,
        @NotNull(message = "必须指定教授") Long professorId,
        List<Long> imageFileIds
) {
}
