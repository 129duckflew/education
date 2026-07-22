package cn.duckflew.enums;


@SuppressWarnings("AlibabaClassMustHaveAuthor")
public enum UserType {

    GUEST(1),PRE_PROFESSOR(2),PROFESSOR(3);

    private int code;
    UserType(int code)
    {
        this.code=code;
    }
    public int getCode() {
        return code;
    }
}
