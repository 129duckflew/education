package cn.duckflew.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
public class AnswerParams
{
    /**
     * 问题的id
     */
    @NotNull(message = "问题id不能为空")
    private Integer questionId;
    @Length(max = 1000,message = "回答内容长度不能超过1000")
    @NotBlank(message = "回答内容不能为空")
    private String answerText;
}
