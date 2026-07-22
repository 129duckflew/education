package cn.duckflew.exception;

import lombok.Data;

@Data
public class OrderIdInvalidException extends RuntimeException
{
    private String errorOrderId;

    public OrderIdInvalidException (String message,String errorOrderId)
    {
        super(message);
        this.errorOrderId=errorOrderId;
    }
}
