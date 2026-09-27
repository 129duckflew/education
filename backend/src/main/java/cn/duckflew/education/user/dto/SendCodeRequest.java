package cn.duckflew.education.user.dto;

import cn.duckflew.education.user.VerificationCode;
import cn.duckflew.education.user.VerificationPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendCodeRequest(
        @NotBlank(message = "接收方不能为空") String target,
        @NotNull(message = "渠道不能为空") VerificationCode.Channel channel,
        @NotNull(message = "用途不能为空") VerificationPurpose purpose
) {
}
