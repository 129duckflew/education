package cn.duckflew.exception;

public class QuestionIdInvalidException extends RuntimeException
{
    private Integer questionId;
    public QuestionIdInvalidException(String message,Integer questionId)
    {
        super(message);
        this.questionId=questionId;
    }

    public Integer getQuestionId()
    {
        return questionId;
    }
}
