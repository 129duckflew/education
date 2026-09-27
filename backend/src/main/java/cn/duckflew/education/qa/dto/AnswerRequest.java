package cn.duckflew.education.qa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerRequest(
        @NotNull(message = "问题 id 不能为空") Long questionId,
        @NotBlank(message = "回答内容不能为空") String content
) {
}
