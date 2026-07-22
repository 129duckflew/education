package cn.duckflew.enums;

/**
 * RabbitMq中监听到的需要发送的邮件的类型
 */
public enum RabbitMailType
{
    /**
     * 注册邮件
     */
    REGISTER_MAIL(0),
    /**
     * 绑定邮箱验证邮件
     */
    BIND_MAIL(1),

    CHANGE_PASSWORD_MAIL(2),

    LOGIN_CHECK_MAIL(3);

    private Integer code;

    RabbitMailType(int code)
    {
        this.code=code;
    }

    public Integer getCode()
    {
        return code;
    }
}
