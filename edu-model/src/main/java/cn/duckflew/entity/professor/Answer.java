package cn.duckflew.entity.professor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
@TableName("answer")
public class Answer
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer questionId;
    private Integer professorId;
    private String  answerText;
    private Date createTime;
    private Integer answerStatus;
    private Date updateTime;
}
