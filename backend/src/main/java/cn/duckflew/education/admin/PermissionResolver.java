package cn.duckflew.education.admin;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 解析后台用户的权限码集合（管理员 RBAC）。
 */
@Service
public class PermissionResolver {

    private final AdminRoleRepository adminRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    public PermissionResolver(AdminRoleRepository adminRoleRepository,
                              RolePermissionRepository rolePermissionRepository,
                              PermissionRepository permissionRepository) {
        this.adminRoleRepository = adminRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    public Set<String> permissionsOf(Long userId) {
        List<Long> roleIds = adminRoleRepository.findByUserId(userId).stream()
                .map(AdminRole::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return Set.of();
        }
        Set<Long> permissionIds = rolePermissionRepository.findByRoleIdIn(roleIds).stream()
                .map(RolePermission::getPermissionId).collect(Collectors.toSet());
        if (permissionIds.isEmpty()) {
            return Set.of();
        }
        return permissionRepository.findAllById(permissionIds).stream()
                .map(Permission::getCode).collect(Collectors.toSet());
    }
}
