package cn.duckflew.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.duckflew.entity.admin.Role;
import cn.duckflew.service.admin.AdminService;
import cn.duckflew.service.admin.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Component
@Slf4j
public class PermissionConfig  implements StpInterface{
    @Autowired
    AdminService adminService;

    @Override
    public List<String> getPermissionList(Object id, String s) {
        Integer userId = Integer.parseInt((String) id);
        List<String> permissionList = adminService.getPermissionList(userId);
        log.info("adminId:{}鉴权,权限码:{}",userId,permissionList.toString());
        return permissionList;
    }

    @Autowired
    RoleService roleService;
    @Override
    public List<String> getRoleList(Object id, String s) {
        Integer adminId = (Integer) id;
        List<Integer> rids = adminService.adminRoleIdList(adminId);
        List<Role> roles = roleService.listByIds(rids);
        return roles.stream().map(Role::getName).collect(Collectors.toList());
    }
}
