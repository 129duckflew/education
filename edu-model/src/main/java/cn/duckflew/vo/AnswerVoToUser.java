package cn.duckflew.vo;

import cn.duckflew.entity.professor.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerVoToUser
{
    private Answer answer;
    /**
     * 点赞数
     */
    private Integer likeNum;
    /**
     * 收藏数
     */
    private Integer collectNum;
    /**
     * 教授名
     */
    private String professorName;
    /**
     * 我是否点赞 1代表已经点赞，2代表未点赞
     */
    private boolean isILike;
    /**
     * 我是否收藏
     */
    private boolean isICollect;
}
