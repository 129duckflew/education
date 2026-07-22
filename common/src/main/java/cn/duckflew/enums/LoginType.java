package cn.duckflew.enums;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
public enum LoginType {

    EMAIL_LOGIN(1),TELEPHONE_LOGIN(2),USERNAME_LOGIN(3),TELEPHONE_CHECK_CODE_LOGIN(4);

    private int code;

    LoginType(int code)
    {
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
