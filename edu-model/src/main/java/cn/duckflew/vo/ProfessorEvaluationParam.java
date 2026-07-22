package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessorEvaluationParam
{
    /**
     * 专家id
     */
    @NotNull(message = "教授id不能为空")
    private Integer professorId;
    /**
     * 评价内容
     */
    @NotBlank(message = "评价内容不能为空")
    private String evaluation;
    /**
     * 打分: 1-5
     */
    @Max(value = 10,message = "最高分为5")
    @Min(value = 0,message = "最低分为1")
    @NotNull(message = "打分不能为空")
    private Integer star;

}
