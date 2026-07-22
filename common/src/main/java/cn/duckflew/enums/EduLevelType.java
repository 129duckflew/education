package cn.duckflew.enums;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
public enum EduLevelType
{
    /**
     * 非全日制
     */
    NOT_ALL_DAY(0),
    /**
     * 全日制
     */
    ALL_DAY(1);

    private int code;

    EduLevelType(int code)
    {
        this.code=code;
    }

    public int getCode()
    {
        return code;
    }
}
