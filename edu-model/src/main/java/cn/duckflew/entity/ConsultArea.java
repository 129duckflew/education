package cn.duckflew.entity;

import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@TableName("t_consult_area")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsultArea
{
    /**
     * 咨询领域 id
     */
    @NotBlank(groups = Update.class,message = "更新必须指定id")
    @Null(groups = AddGroup.class,message = "新增不能指定id")
    private String id;
    /**
     * 领域名称
     */
    @NotBlank(groups = {AddGroup.class,UpdateGroup.class},message = "领域名称不能为空")
    private String areaName;
    /**
     * 父级领域id
     * @since 顶级领域则把parentId设置为0
     */
    @NotBlank(groups = AddGroup.class,message = "父级id不能为空")
    @Null(groups = UpdateGroup.class,message = "更新不需要指定parentId")
    private String parentId;
}
