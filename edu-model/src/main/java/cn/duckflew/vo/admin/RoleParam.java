package cn.duckflew.vo.admin;

import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleParam
{

    /**
     * 角色id
     */
    @Null(message = "添加不能指定id",groups = AddGroup.class)
    @NotNull(message = "更新必须指定id",groups = UpdateGroup.class)
    private Integer roleId;
    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    private String roleName;
    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    private String nameZh;

    /**
     * 菜单id列表
     */
    @NotEmpty(message = "菜单Id列表不能为空")
    private List<Integer> menuIdList;


}
