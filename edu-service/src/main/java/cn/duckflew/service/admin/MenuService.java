package cn.duckflew.service.admin;

import cn.duckflew.entity.admin.Menu;
import cn.duckflew.mapper.admin.MenuMapper;
import cn.duckflew.vo.admin.MenuVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Service
public class MenuService extends ServiceImpl<MenuMapper, Menu> {


    @Autowired
    MenuMapper menuMapper;
    @Cacheable(cacheNames = "allMenuVo")
    public List<MenuVo> allMenuVo() {
        List<Menu> all = menuMapper.selectList(null);
        return menuListToVoList(all);
    }



    public List<MenuVo> menuListToVoList(List<Menu> menus)
    {
        List<MenuVo> res=new ArrayList<>();
        for (Menu menu : menus)
        {
            MenuVo vo = new MenuVo(menu);
            if (menu.getParentId().equals(0))
            {
                res.add(vo);
            }
            for (Menu child : menus)
            {
                if (child.getParentId().equals(vo.getId()))
                {
                    MenuVo childVo = new MenuVo(child);
                    List<MenuVo> children = vo.getChildren();
                    if (children==null)children=new ArrayList<>();
                    children.add(childVo);
                    vo.setChildren(children);
                }
            }
        }
        return res;
    }

}
