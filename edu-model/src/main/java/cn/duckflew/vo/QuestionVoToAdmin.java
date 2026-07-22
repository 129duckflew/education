package cn.duckflew.vo;

import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.Question;
import lombok.Getter;
import lombok.Setter;


public class QuestionVoToAdmin extends Question
{
    /**
     * 回答数
     */
    @Getter
    @Setter
    private Integer ansNum;
    /**
     *  点赞数
     */
    @Getter
    @Setter
    private Integer likeNum;

    /**
     * 关联的领域
     */
    @Getter
    @Setter
    private ConsultArea relatedArea;
}
