package cn.duckflew.education.user.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserProfile user
) {
    public static TokenResponse of(String accessToken, String refreshToken, long expiresIn, UserProfile user) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
