package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.vo.admin.MenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.duckflew.service.admin.MenuService;

import java.util.List;

/**
 * 后台管理: 菜单相关接口
 */
@SuppressWarnings("AlibabaClassMustHaveAuthor")
@RestController
@RequestMapping("/menu")
public class MenuController {


    @Autowired
    MenuService menuService;

    /**
     * 获取所有菜单以及菜单需要的权限
     * @return
     */
    @GetMapping("/")
    @SaCheckPermission("role")
    public SaResult getAllPermission()
    {
        List<MenuVo> all = menuService.allMenuVo();
        return SaResult.ok().setData(all);
    }

}
