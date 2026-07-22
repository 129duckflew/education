package cn.duckflew.entity.professor;

import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("study_resource")
public class StudyResource
{
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @NotNull(groups = UpdateGroup.class,message = "更新必须指定id")
    @Null(groups = AddGroup.class,message = "添加时不允许指定id")
    private Integer id;
    /**
     * 文件id
     */
    @NotBlank(groups = AddGroup.class,message = "新增必须指定文件id")
    private String fileId;
    @Null(groups ={AddGroup.class,UpdateGroup.class} ,message = "不允许指定professorId")
    private Integer professorId;
    @Null(groups ={AddGroup.class,UpdateGroup.class} ,message = "不需要指定日期")
    private Date createTime;
    @NotBlank(groups = AddGroup.class,message = "新增必须指定资源名称")
    private String resourceName;
    /**
     * 备注
     */
    private String remark;
}
