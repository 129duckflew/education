package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

public class EduDataSourceIdInvalidException extends RuntimeException
{
    @Getter
    @Setter
    private Integer errorId;
    public EduDataSourceIdInvalidException(String message, Integer id)
    {
        super(message);
        this.errorId=id;
    }
}
