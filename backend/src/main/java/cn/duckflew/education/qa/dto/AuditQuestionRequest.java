package cn.duckflew.education.qa.dto;

import cn.duckflew.education.qa.QuestionStatus;
import jakarta.validation.constraints.NotNull;

public record AuditQuestionRequest(
        @NotNull(message = "问题 id 不能为空") Long questionId,
        @NotNull(message = "审核状态不能为空") QuestionStatus status
) {
}
