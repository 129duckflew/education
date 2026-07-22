package cn.duckflew.vo.admin;

import cn.duckflew.entity.admin.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuVo extends Menu
{
    private List<MenuVo> children;
    public MenuVo(Menu menu)
    {
        setId(menu.getId());
        setName(menu.getName());
        setParentId(menu.getParentId());
        setLeaf(menu.isLeaf());
        setPermissionCode(menu.getPermissionCode());
    }
}
