package cn.duckflew.exception;

import lombok.Getter;

@Getter
public class AnswerNotExistException extends RuntimeException
{
    private Integer answerId;

    public AnswerNotExistException(String message,Integer answerId)
    {
        super(message);
        this.answerId=answerId;
    }

}
