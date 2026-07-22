package cn.duckflew.exception;

public class TelephoneExistException extends RuntimeException
{
    private String number;
    public TelephoneExistException(String message,String number)
    {
        super(message);
        this.number=number;
    }

    public String getNumber()
    {
        return number;
    }
}
