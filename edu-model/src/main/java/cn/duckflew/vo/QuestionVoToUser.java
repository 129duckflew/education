package cn.duckflew.vo;

import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.Question;
import lombok.*;

import java.util.List;

public class QuestionVoToUser extends Question
{
    /**
     * 回答数
     */
    @Getter
    @Setter
    private Integer ansNum;
    /**
     * 点赞数
     */
    @Getter
    @Setter
    private Integer likeNum;
    /**
     * 我是否点赞了
     */
    @Getter
    @Setter
    private boolean iLike;
    @Getter
    @Setter
    private List<ConsultArea> consultAreaList;

}