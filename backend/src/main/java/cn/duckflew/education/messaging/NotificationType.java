package cn.duckflew.education.messaging;

/**
 * 通知类型。取代旧的 13 种魔法数字。
 */
public enum NotificationType {
    SYSTEM,
    QUESTION_RECEIVED,
    QUESTION_APPROVED,
    QUESTION_REJECTED,
    QUESTION_FORBIDDEN,
    ANSWER_RECEIVED,
    ANSWER_LIKED,
    ANSWER_COLLECTED,
    QUESTION_LIKED,
    PROFESSOR_APPROVED,
    PROFESSOR_REJECTED
}
