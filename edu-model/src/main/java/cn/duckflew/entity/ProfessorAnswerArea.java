package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@TableName("professor_answer_area")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessorAnswerArea
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String consultAreaId;
    private Integer professorId;
}
