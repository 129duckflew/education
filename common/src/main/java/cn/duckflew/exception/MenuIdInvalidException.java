package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuIdInvalidException extends RuntimeException
{
    private Integer errorId;
    public MenuIdInvalidException(String message,Integer errorId)
    {
        super(message);
        this.errorId=errorId;
    }
}
