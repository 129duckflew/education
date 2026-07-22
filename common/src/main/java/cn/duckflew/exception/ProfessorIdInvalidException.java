package cn.duckflew.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorIdInvalidException extends RuntimeException
{
    private Integer professorId;
    public ProfessorIdInvalidException(String message, Integer professorId)
    {
        super(message);
        this.professorId=professorId;
    }
}
