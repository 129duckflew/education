package cn.duckflew.vo;

import cn.duckflew.entity.ConsultFeedback;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class ConsultFeedbackVo extends ConsultFeedback
{
    public ConsultFeedbackVo(ConsultFeedback cf)
    {
        super(cf.getId(), cf.getProfessorId(), cf.getUserId(), cf.getStar(), cf.getCreateTime(), cf.getEvaluation());
    }
    private String userNickName;
}
