package cn.duckflew.exception;

public class NotProfessorException extends RuntimeException
{
    private Integer errProId;
    public NotProfessorException(String message, Integer proId)
    {
        super(message);
        this.errProId=proId;
    }

    public Integer getErrProId()
    {
        return errProId;
    }
}
