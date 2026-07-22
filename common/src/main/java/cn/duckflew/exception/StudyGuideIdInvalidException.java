package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyGuideIdInvalidException extends RuntimeException
{
    private String errGuideId;
    public StudyGuideIdInvalidException(String message, String guideId)
    {
        super(message);
        this.errGuideId=guideId;
    }
}
