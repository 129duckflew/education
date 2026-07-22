package cn.duckflew.entity.professor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Degree
{
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 学位名称
     */
    private String degreeName;
}
