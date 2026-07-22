package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

public class NotPreProfessorException extends RuntimeException
{
    @Getter
    @Setter
    private Integer userId;

    public NotPreProfessorException(String message,Integer userId)
    {
        super(message) ;
        this.userId=userId;
    }
}
