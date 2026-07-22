package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("question_area")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionArea
{
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String consultAreaId;

    private Integer questionId;
}
