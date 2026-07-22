package cn.duckflew.entity;

import cn.duckflew.validate.group.AddGroup;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@TableName("research_direction")
@NoArgsConstructor
public class ResearchDirection
{
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @Null(groups = AddGroup.class,message = "添加不允许指定id")
    private Integer id;

    /**
     * 研究方向名称
     */
    @NotBlank(groups = AddGroup.class,message = "必须指定研究方向名")
    private String directionName;
    /**
     * 备注
     */
    private Integer extra;

    /**
     * 专业id
     */
    @NotNull(groups = AddGroup.class,message = "必须指定关联的本科专业id")
    private Integer majorId;
}
