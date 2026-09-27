package cn.duckflew.education.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Size(min = 3, max = 32, message = "用户名长度需在 3-32 之间") String username,
        @Email(message = "邮箱格式不正确") String email,
        @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确") String phone,
        @NotBlank(message = "密码不能为空") @Size(min = 6, max = 64, message = "密码长度需在 6-64 之间") String password,
        String code
) {
}
