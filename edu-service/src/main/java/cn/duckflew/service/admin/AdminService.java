package cn.duckflew.service.admin;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.admin.*;
import cn.duckflew.enums.SearchLogic;
import cn.duckflew.exception.AdminNotExistException;
import cn.duckflew.mapper.admin.*;
import cn.duckflew.vo.admin.AdminVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Service
@Slf4j
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    @Autowired
    AdminMapper adminMapper;
    public SaResult login(String username, String password) {

        log.debug("md5:{}",SaSecureUtil.md5("CHANGE_ME"));
        Admin admin=adminMapper.selectOne(
                new QueryWrapper<Admin>().
                        eq("username",username).
                        eq("password", SaSecureUtil.md5(password)));
        if (admin!=null)
        {
            StpUtil.login(admin.getId());
            return SaResult.ok().setData(StpUtil.getTokenInfo());
        }
        return SaResult.error().setCode(400).setMsg("用户名或者密码错误");
    }


    @Autowired
    AdminRoleMapper adminRoleMapper;
    @Autowired
    RoleMenuMapper roleMenuMapper;
    @Autowired
    MenuMapper menuMapper;
    public List<Menu> adminMenuList(Integer adminId)
    {
        log.info("adminId={},查询MenuList",adminId);
        Set<Integer> menuIdList=new HashSet<>();
        List<Integer> roleIdList = adminRoleIdList(adminId);
        for (Integer roleId : roleIdList)
        {
            /**
             * 如果有超级管理员角色
             */
            if (roleId.equals(1))return menuMapper.selectList(null);
            log.info("roleId:{}",roleId);
            List<Integer> menuIds = roleMenuMapper.selectList(new QueryWrapper<RoleMenu>().eq("role_id", roleId)).stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
            menuIdList.addAll(menuIds);
        }
        return menuMapper.selectBatchIds(menuIdList);
    }

    public List<Integer> adminRoleIdList(Integer adminId)
    {
        return adminRoleMapper.selectList(new QueryWrapper<AdminRole>().eq("admin_id", adminId)).stream().map(AdminRole::getRoleId).collect(Collectors.toList());
    }
    public List<String> getPermissionList(Integer adminId) {
        List<Integer> roleIdList =  adminRoleIdList(adminId);
        if (roleIdList.contains(1))
        {
            log.info("超级管理员授权,赋予所有权限");
            return menuMapper.selectList(null)
                    .stream().filter(menu->StringUtils.isNotBlank(menu.getPermissionCode()))
                    .map(Menu::getPermissionCode).collect(Collectors.toList());
        }
        if (roleIdList.isEmpty())return new ArrayList<>();
        Set<Integer> menuIdSet=roleMenuMapper.selectList(
                new QueryWrapper<RoleMenu>()
                .in("role_id",roleIdList)
        ).stream().map(RoleMenu::getMenuId).collect(Collectors.toSet());
        log.info("具有权限的菜单列表:{}",menuIdSet.toString());
        return menuMapper.selectBatchIds(menuIdSet)
                .stream().map(Menu::getPermissionCode).collect(Collectors.toList());
    }
    @Transactional(rollbackFor = Exception.class)
    public void addAdmin(AdminVo adminVo)
    {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminVo,admin);
        adminMapper.insert(admin);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAdmin(AdminVo adminVo)
    {
        Admin exist = adminMapper.selectById(adminVo.getId());
        if (exist==null)throw new AdminNotExistException();
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminVo,admin);
        adminMapper.updateById(admin);
    }

    public void  searchAdmin(Page<Admin> page, String keyword, SearchLogic searchLogic, String ... fields)
    {
        QueryWrapper<Admin> qw = new QueryWrapper<>();
        for (int i = 0; i < fields.length; i++)
        {
            if (i==0)
            {
                qw.like(fields[i],keyword);
                continue;
            }
            if (searchLogic.equals(SearchLogic.OR))
                qw.or();
            qw.like(fields[i],keyword);
        }
        adminMapper.selectPage(page,qw);
    }
}
