package cn.duckflew.enums;

public enum UserOpType
{

    LIKE_ANSWER(0),COLLECT_ANSWER(1),COLLECT_PROFESSOR(2),LIKE_QUESTION(3)
    ,COLLECT_QUESTION(4);
    private int code;

    UserOpType(int code)
    {
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
