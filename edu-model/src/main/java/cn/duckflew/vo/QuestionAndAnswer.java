package cn.duckflew.vo;

import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAndAnswer extends Question
{
    private AnswerVoToUser answer;
    private List<ConsultArea> consultAreaList;
}
