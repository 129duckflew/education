package cn.duckflew.exception;

import lombok.Getter;

public class JobRankIdInvalidException extends RuntimeException
{
    @Getter
    private Integer errorJobRankId;
    public JobRankIdInvalidException(String message, Integer jobRankId)
    {
        super(message);
        this.errorJobRankId=jobRankId;
    }
}
