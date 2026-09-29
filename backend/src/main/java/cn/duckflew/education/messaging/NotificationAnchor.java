package cn.duckflew.education.messaging;

/**
 * 通知深链锚点：描述通知应定位到问题页里的哪个元素。
 * <ul>
 *     <li>{@link #ANSWER} — 定位到某条回答</li>
 *     <li>{@link #QUESTION_COMMENT} — 定位到问题下的某条评论</li>
 *     <li>{@link #ANSWER_COMMENT} — 定位到某条回答下的评论（需 anchor_ref_id 展开评论区）</li>
 * </ul>
 */
public enum NotificationAnchor {
    ANSWER,
    QUESTION_COMMENT,
    ANSWER_COMMENT
}
