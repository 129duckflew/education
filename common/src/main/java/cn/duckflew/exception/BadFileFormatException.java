package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BadFileFormatException extends RuntimeException
{
    private String badFileSuffix;
    public BadFileFormatException(String message,String badFileSuffix)
    {
        super(message)  ;
        this.badFileSuffix=badFileSuffix;
    }

}
