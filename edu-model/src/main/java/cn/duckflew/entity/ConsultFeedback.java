package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@TableName("consult_feedback")
@NoArgsConstructor
public class ConsultFeedback
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer professorId;
    private Integer userId;
    /**
     * 评分：0-10
     */
    private Integer star;
    private Date createTime;
    /**
     * 评价内容
     */
    private String evaluation;
}
