package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AreaNameExistException extends RuntimeException
{
    private String badAreaName;
    public AreaNameExistException(String message,String badAreaName)
    {
        super(message)  ;
        this.badAreaName=badAreaName;
    }
}
