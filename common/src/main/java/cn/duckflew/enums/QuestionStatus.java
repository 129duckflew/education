package cn.duckflew.enums;

public enum  QuestionStatus
{
    /**
     * 被封禁的问题
     */
    FORBIDDEN(0),
    /**
     * 正常
     */
    NORMAL(1),
    /**
     * 待审核
     */
    AUDITING(2),
    /**
     * 审核不通过
     */
    AUDIT_FAILURE(3),
    /**
     * 提问用户选择不公开
     */
    NOT_PUBLIC(4);
    private int code;

    QuestionStatus(int code)
    {
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
