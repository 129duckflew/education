package cn.duckflew.exception;

public class CheckCodeInvalidException extends RuntimeException
{
    private String checkCode;
    public CheckCodeInvalidException(String message,String checkCode)
    {
        super(message);
        this.checkCode=checkCode;
    }

    public String getCheckCode()
    {
        return checkCode;
    }
}
