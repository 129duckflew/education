package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("question_desc_img")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionDescImg
{

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 文件id
     */
    private String fileId;
    /**
     * 问题id
     */
    private Integer questionId;
}
