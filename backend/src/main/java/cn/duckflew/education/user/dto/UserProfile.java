package cn.duckflew.education.user.dto;

import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRole;

import java.time.Instant;
import java.time.LocalDate;

public record UserProfile(
        Long id,
        String username,
        String email,
        String phone,
        String nickname,
        String realName,
        UserRole role,
        Long avatarFileId,
        String gender,
        LocalDate birthday,
        boolean enabled,
        Instant createdAt
) {
    public static UserProfile from(User user) {
        return new UserProfile(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(),
                user.getNickname(), user.getRealName(), user.getRole(), user.getAvatarFileId(),
                user.getGender(), user.getBirthday(), user.isEnabled(), user.getCreatedAt());
    }
}
