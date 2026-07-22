package cn.duckflew.service.admin;

import cn.duckflew.entity.admin.AdminRole;
import cn.duckflew.entity.admin.Menu;
import cn.duckflew.entity.admin.Role;
import cn.duckflew.entity.admin.RoleMenu;
import cn.duckflew.exception.MenuIdInvalidException;
import cn.duckflew.exception.RoleNameExistException;
import cn.duckflew.exception.RoleNotExistException;
import cn.duckflew.mapper.admin.AdminRoleMapper;
import cn.duckflew.mapper.admin.MenuMapper;
import cn.duckflew.mapper.admin.RoleMapper;
import cn.duckflew.mapper.admin.RoleMenuMapper;
import cn.duckflew.vo.admin.AddRoleParam;
import cn.duckflew.vo.admin.RoleVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    @Autowired
    RoleMenuMapper roleMenuMapper;
    @Autowired
    RoleMapper roleMapper;
    @Transactional(rollbackFor =Exception.class)
    public void  addRole(AddRoleParam roleVo)
    {
        boolean exist=roleMapper.selectOne(
                new QueryWrapper<Role>()
                .eq("name",roleVo.getName())
        )!=null;
        if (exist)throw new RoleNameExistException("添加角色出现roleName重复",roleVo.getName());
        Role role = new Role();
        role.setName(roleVo.getName());
        role.setNameZh(roleVo.getNameZh());
        List<Integer> menuIdList = roleVo.getMenuIdList();
        roleMapper.insert(role);
        if (menuIdList!=null&&!menuIdList.isEmpty())
        {
            for (Integer menuId : menuIdList )
            {
                boolean menuExist=menuMapper.selectById(menuId)!=null;
                if (!menuExist)
                {
                    log.warn("id={}的菜单不存在",menuId);
                    throw new MenuIdInvalidException("菜单id无效",menuId);
                }
                RoleMenu rolePermission = new RoleMenu();
                rolePermission.setMenuId(menuId);
                rolePermission.setRoleId(role.getId());
                roleMenuMapper.insert(rolePermission);
            }
        }
    }

    @Autowired
    AdminRoleMapper adminRoleMapper;
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Integer roleId)
    {
        Role role = roleMapper.selectById(roleId);
        if (role==null)throw new RoleNotExistException();
        roleMenuMapper.delete(
                new QueryWrapper<RoleMenu>()
                .eq("role_id",roleId)
        );
        adminRoleMapper.delete(
                new QueryWrapper<AdminRole>()
                .eq("role_id",roleId)
        );
        roleMapper.deleteById(roleId);
    }

    @Transactional(rollbackFor =Exception.class)
    public void updateRole(Integer roleId, String roleName, List<Integer> menuIdList)
    {
        Role role=roleMapper.selectById(roleId);
        if (role==null)throw new RoleNotExistException();
        role.setName(roleName);
        roleMapper.updateById(role);
        roleMenuMapper.delete(
                new QueryWrapper<RoleMenu>()
                .eq("role_id",roleId)
        );
        for (Integer menuId : menuIdList)
        {
            boolean permissionExist=menuMapper.selectById(menuId)!=null;
            if (!permissionExist)
            {
                log.error("id={}的菜单不存在",menuId);
                throw new MenuIdInvalidException("菜单不存在",menuId);
            }
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            roleMenuMapper.insert(roleMenu);
        }
    }


    @Autowired
    MenuMapper menuMapper;
    public RoleVo roleToVo(Role role)
    {
        RoleVo res = new RoleVo(role);
        List<Integer> menuIds = roleMenuMapper.selectList(new QueryWrapper<RoleMenu>().eq("role_id", role.getId())).stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        List<Menu> menuList = new ArrayList<>();
        if (!menuIds.isEmpty())
            menuList= menuMapper.selectBatchIds(menuIds);
        res.setMenuList(menuList);
        return res;
    }
}
