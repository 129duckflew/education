package cn.duckflew.enums;

public enum  SysMsgType
{
    SYS_BROADCAST(0),PRIVATE_CHAT(1),LIKE_ANSWER(2),COLLECT_ANSWER(3)
    ,GET_ANSWER(4),GET_QUESTION(5),LIKE_QUESTION(6),BAN_QUESTION(7), BAN_ANSWER(8),
    QUESTION_AUDIT_ACCESS(9),QUESTION_AUDIT_NOT_ACCESS(10),ANSWER_AUDIT_ACCESS(12),
    ANSWER_AUDIT_NOT_ACCESS(13),GET_EVALUATION(11);
    private int code;

    SysMsgType(int code)
    {
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
