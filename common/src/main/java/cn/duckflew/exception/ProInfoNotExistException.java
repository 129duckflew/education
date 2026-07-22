package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProInfoNotExistException extends RuntimeException
{

    private Integer userId;
    public ProInfoNotExistException(String message,Integer userId)
    {
        super(message);
        this.userId=userId;
    }
}
