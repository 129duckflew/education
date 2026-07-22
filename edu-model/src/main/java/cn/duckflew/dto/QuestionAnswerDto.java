package cn.duckflew.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerDto
{
    private Integer questionId;
    private Integer professorId;
    private String  answerText;
    private Date createTime;
    private Integer answerStatus;
    private Date updateTime;
}
