package cn.duckflew.education.common.notification;

/**
 * 验证码通知载荷（写入 outbox 的 JSON 结构）。
 */
public record VerificationPayload(String target, String code, String purpose) {
}
