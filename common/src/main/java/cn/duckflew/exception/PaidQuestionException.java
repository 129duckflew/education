package cn.duckflew.exception;

import lombok.Data;

@Data
public class PaidQuestionException  extends RuntimeException
{

    private String errorOrderId;
    public PaidQuestionException(String message,String errorOrderId)
    {
        super(message);
        this.errorOrderId=errorOrderId;
    }
}
