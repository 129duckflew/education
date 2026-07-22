package cn.duckflew.exception;

import lombok.Getter;

@Getter
public class ConsultFeedbackIdInvalidException extends RuntimeException
{
    private Integer errId;
    public ConsultFeedbackIdInvalidException(String message, Integer errId)
    {
        super(message);
        this.errId=errId;
    }
}
