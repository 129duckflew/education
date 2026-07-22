package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileIdInvalidException extends RuntimeException
{
    private String errorFileId;

    public FileIdInvalidException (String message,String errorFileId)
    {
        super(message);
        this.errorFileId=errorFileId;
    }

}
