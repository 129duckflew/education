package cn.duckflew.education.user.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(
        @Size(max = 64) String nickname,
        @Size(max = 64) String realName,
        String gender,
        LocalDate birthday
) {
}
