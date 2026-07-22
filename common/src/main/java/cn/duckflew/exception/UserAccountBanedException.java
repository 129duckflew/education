package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountBanedException extends RuntimeException
{
    private Integer userId;
    public UserAccountBanedException(String message,Integer userId)
    {
        super(message);
        this.userId=userId;
    }
}
