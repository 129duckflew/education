package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditQuestionParam
{
    /**
     * 问题的id
     */
    @NotNull(message = "问题id不能为空")
    private Integer questionId;
    /**
     * 问题状态 questionStatus含义: 0:被封禁 1:正常 2:待审核/审核中 3:审核不通过
     */
    @NotNull(message = "问题状态代码不能为空")
    private Integer questionStatus;
    /**
     * 备注
     */
    private String comment;
}
