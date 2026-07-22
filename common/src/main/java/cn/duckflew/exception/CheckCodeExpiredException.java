package cn.duckflew.exception;

public class CheckCodeExpiredException extends RuntimeException
{
    private String checkCode;
    public CheckCodeExpiredException(String message,String checkCode)
    {
        super(message);
        this.checkCode=checkCode;
    }

    public String getCheckCode()
    {
        return checkCode;
    }
}
