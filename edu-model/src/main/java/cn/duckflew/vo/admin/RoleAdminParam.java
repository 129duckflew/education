package cn.duckflew.vo.admin;

import cn.duckflew.validate.group.UpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAdminParam
{


    /**
     * 管理员id
     */
    @NotNull(groups = UpdateGroup.class,message = "必须指定管理员id")
    private Integer adminId;
    /**
     * 角色Id
     */
    @NotNull(message = "角色Id数组不能为空")
    private List<Integer> roleIdList;




}
