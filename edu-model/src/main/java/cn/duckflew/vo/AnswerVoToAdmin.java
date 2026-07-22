package cn.duckflew.vo;

import cn.duckflew.entity.Question;
import cn.duckflew.entity.professor.Answer;
import cn.duckflew.entity.user.BaseUser;
import lombok.Getter;
import lombok.Setter;

public class AnswerVoToAdmin extends Answer
{
    /**
     * 问题
     */
    @Setter
    @Getter
    private Question question;

    /**
     * 点赞数
     */
    @Setter
    @Getter
    private Integer likeNum;

    /**
     * 收藏数
     */
    @Setter
    @Getter
    private Integer collectNum;


    /**
     * 作者
     */
    @Getter
    @Setter
    private BaseUser author;

}
