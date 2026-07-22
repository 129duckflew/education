package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.admin.Role;
import cn.duckflew.service.admin.RoleService;
import cn.duckflew.validate.group.UpdateGroup;
import cn.duckflew.vo.admin.AddRoleParam;
import cn.duckflew.vo.admin.RoleParam;
import cn.duckflew.vo.admin.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台管理:角色相关接口
 */
@RestController
@RequestMapping("/role")
public class RoleController
{
    @Autowired
    RoleService roleService;
    /**
     * 添加角色
     * @param addRoleParam 参数
     * @return
     */
    @PostMapping
    @SaCheckLogin
    @SaCheckPermission("role")
    public SaResult addRole(@RequestBody @Validated AddRoleParam addRoleParam)
    {
        roleService.addRole(addRoleParam);
        return SaResult.ok().setMsg("添加成功");
    }

    /**
     * 根据id删除角色
     * @param roleId
     * @return
     */
    @DeleteMapping("/{roleId}")
    @SaCheckLogin
    @SaCheckPermission("role")
    public SaResult deleteRole(@PathVariable Integer roleId)
    {
        roleService.deleteRole(roleId);
        return SaResult.ok().setMsg("删除角色成功");
    }

    /**
     * 更新角色信息
     * @param roleParam
     * @return
     */
    @PutMapping("/{roleId}")
    @SaCheckLogin
    @SaCheckPermission("role")
    public SaResult updateRole(@RequestBody @Validated({UpdateGroup.class}) RoleParam roleParam)
    {
        roleService.updateRole(roleParam.getRoleId(), roleParam.getRoleName(), roleParam.getMenuIdList());
        return SaResult.ok().setMsg("更新角色成功");
    }

    /**
     * 获取所有角色不附带菜单信息
     * @apiNote 这个接口用于给后台用户授权时使用
     */
    @GetMapping("/")
    @SaCheckLogin
    @SaCheckPermission("admin")
    public SaResult getAllRole()
    {
        List<Role> all = roleService.list(null);
        return SaResult.ok().setData(all);
    }
    /**
     * 获取所有角色(不包含超级管理员)以及关联的菜单id
     */
    @GetMapping("/all")
    @SaCheckPermission("role")
    public SaResult getAllRoleVo()
    {
        List<RoleVo> all = roleService.list(null)
                .stream().filter(role->!role.getId().equals(1)).map(role->roleService.roleToVo(role)).collect(Collectors.toList());
        return SaResult.ok().setData(all);
    }

}
