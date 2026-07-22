package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadException extends Throwable
{
    private Integer userId;
    public FileUploadException(String message, Integer userId)
    {
        super(message);
        this.userId=userId;
    }
}
