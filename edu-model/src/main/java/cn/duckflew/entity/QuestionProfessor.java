package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
@TableName("t_question_professor")
public class QuestionProfessor
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer questionId;
    private Integer professorId;
}
