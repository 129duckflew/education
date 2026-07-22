package cn.duckflew.exception;

import lombok.Data;

public class BadCreditException extends RuntimeException {
    private String errorPwd;
    public BadCreditException(String message,String errorPwd)
    {
        super(message);
        this.errorPwd=errorPwd;
    }

    public String getErrorPwd()
    {
        return errorPwd;
    }
}
