package cn.duckflew.vo.admin;

import cn.duckflew.entity.admin.Menu;
import cn.duckflew.entity.admin.Role;
import cn.duckflew.validate.group.AddRoleGroup;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Null;
import java.util.List;


@Getter
@Setter
public class RoleVo extends Role
{
    @Null(groups = AddRoleGroup.class)
    private List<Menu> menuList;
    public RoleVo(Role role)
    {
        setId(role.getId());
        setName(role.getName());
        setNameZh(role.getNameZh());
    }
}
