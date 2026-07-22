package cn.duckflew.service.admin;

import cn.duckflew.entity.admin.Admin;
import cn.duckflew.entity.admin.AdminRole;
import cn.duckflew.entity.admin.Role;
import cn.duckflew.enums.SearchLogic;
import cn.duckflew.exception.AdminNotExistException;
import cn.duckflew.exception.RoleNotExistException;
import cn.duckflew.mapper.admin.AdminMapper;
import cn.duckflew.mapper.admin.AdminRoleMapper;
import cn.duckflew.mapper.admin.RoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Service
public class AdminRoleService extends ServiceImpl<AdminRoleMapper, AdminRole>
{

    @Autowired
    AdminRoleMapper adminRoleMapper;
    @Autowired
    AdminMapper adminMapper;
    @Autowired
    RoleMapper roleMapper;
    @Transactional(rollbackFor = Exception.class)
    public void addRoleToAdmin(Integer adminId, List<Integer> roleIdList)
    {
        Admin admin = adminMapper.selectById(adminId);
        if (admin==null) throw new AdminNotExistException();
        adminRoleMapper.delete(
                new QueryWrapper<AdminRole>()
                .eq("admin_id",adminId)
        );
        for (Integer roleId : roleIdList)
        {
            Role role = roleMapper.selectById(roleId);
            if (role==null)throw new RoleNotExistException();
            AdminRole adminRole = new AdminRole();
            adminRole.setRoleId(roleId);
            adminRole.setAdminId(adminId);
            adminRoleMapper.insert(adminRole);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRoleToAdmin(Integer adminId, List<Integer> roleIdList)
    {
        adminRoleMapper.delete(
                new QueryWrapper<AdminRole>()
                .eq("admin_id",adminId)
        );
        addRoleToAdmin(adminId,roleIdList);
    }

    public List<Role> getAdminRole(Integer adminId)
    {
        List<Integer> roleIdList = adminRoleMapper.selectList(new QueryWrapper<AdminRole>().eq("admin_id", adminId)).stream().map(AdminRole::getRoleId).collect(Collectors.toList());
        if (roleIdList.isEmpty())return new ArrayList<>();
        List<Role> res = roleMapper.selectBatchIds(roleIdList);
        return res;
    }

}
