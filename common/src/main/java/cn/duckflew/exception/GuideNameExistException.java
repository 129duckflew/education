package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuideNameExistException extends RuntimeException
{
    private String studyGuideName;
    public GuideNameExistException(String message, String studyGuideName)
    {
        super(message)  ;
        this.studyGuideName=studyGuideName;
    }
}
