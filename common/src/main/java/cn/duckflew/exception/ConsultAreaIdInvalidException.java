package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultAreaIdInvalidException extends RuntimeException
{
    private String errorCaId;
    public ConsultAreaIdInvalidException(String message,String errorCaId)
    {
        super(message);
        this.errorCaId=errorCaId;
    }
}
