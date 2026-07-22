package cn.duckflew.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.admin.Admin;
import cn.duckflew.entity.admin.Role;
import cn.duckflew.enums.SearchLogic;
import cn.duckflew.service.QuestionService;
import cn.duckflew.service.admin.AdminRoleService;
import cn.duckflew.service.admin.AdminService;
import cn.duckflew.service.BaseUserService;
import cn.duckflew.service.admin.MenuService;
import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import cn.duckflew.vo.admin.AdminVo;
import cn.duckflew.vo.admin.MenuVo;
import cn.duckflew.vo.admin.RoleAdminParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台管理: 管理员用户管理
 */
@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController
{

    @Autowired
    AdminService adminService;

    @Autowired
    BaseUserService baseUserService;
    @Autowired
    QuestionService questionService;
    @Autowired
    MenuService menuService;

    @Autowired
    AdminRoleService adminRoleService;

    /**
     * 添加管理员
     * @param adminVo 参数
     * @return
     */
    @PostMapping("/")
    @SaCheckLogin
    @SaCheckPermission("admin")
    public SaResult addAdmin(@RequestBody @Validated({AddGroup.class}) AdminVo adminVo)
    {
        adminVo.setEnabled(true);
        adminService.addAdmin(adminVo);
        return SaResult.ok();
    }

    /**
     * 获取所有管理员信息
     * @param pageNum 分页数 默认为0
     * @param pageSize 分页大小 默认为5
     * @return
     */
    @GetMapping("/")
    @SaCheckLogin
    @SaCheckPermission("admin")
    public SaResult allAdmin(
            @RequestParam(defaultValue = "0",required = false)
            Integer pageNum,
            @RequestParam(defaultValue = "5",required = false)
            Integer pageSize
    )
    {
        Page<Admin> page = new Page<>(pageNum, pageSize);
        adminService.page(page);
        List<Admin> records = page.getRecords();
        records.forEach(admin -> admin.setPassword(null));
        return SaResult.ok().setData(page);
    }
    /**
     * 修改管理员信息
     * @param adminVo
     * @return
     * @apiNote 包括禁用，头像手机号等等，都可以,需要改什么就传递什么参数，除了id必填以外其他都是选填
     */
    @PutMapping("/")
    @SaCheckLogin
    @SaCheckPermission("admin")
    public SaResult updateAdmin(@RequestBody @Validated({UpdateGroup.class}) AdminVo adminVo)
    {
        adminService.updateAdmin(adminVo);
        return SaResult.ok();
    }


    /**
     * 给管理员添加角色
     * @param addRoleAdminParam
     * @return
     */
    @SaCheckLogin
    @PostMapping("/role")
    @SaCheckPermission("admin")
    public SaResult addRoleToAdmin(@RequestBody @Validated RoleAdminParam addRoleAdminParam)
    {
        adminRoleService.addRoleToAdmin(addRoleAdminParam.getAdminId(),addRoleAdminParam.getRoleIdList());
        return SaResult.ok().setMsg("添加用户角色成功");
    }
    /**
     * 获取管理员的角色
     * @param adminId 管理员id
     * @return
     */
    @SaCheckLogin
    @GetMapping("/role/{adminId}")
    @SaCheckPermission("admin")
    public SaResult getAdminRole(@PathVariable Integer adminId)
    {
        List<Role> roles=adminRoleService.getAdminRole(adminId);
        return SaResult.ok().setData(roles);
    }

    /**
     * 修改管理员角色
     * @param addRoleAdminParam
     * @return
     */
    @SaCheckLogin
    @PutMapping("/role")
    @SaCheckPermission("admin")
    public SaResult UpdateRoleToAdmin(@RequestBody @Validated RoleAdminParam addRoleAdminParam)
    {
        adminRoleService.updateRoleToAdmin(addRoleAdminParam.getAdminId(),addRoleAdminParam.getRoleIdList());
        return SaResult.ok().setMsg("修改用户角色成功");
    }


    /**
     * 根据真名或者昵称来搜搜
     * @param pageNum
     * @param pageSize
     * @param keyword 搜索关键词
     * @return
     */
    @SaCheckLogin
    @GetMapping("/search/by_name")
    @SaCheckPermission("admin")
    public SaResult searchAdminByName(
            @RequestParam(required = false,defaultValue = "0")
            Integer pageNum,
            @RequestParam(required = false,defaultValue = "5")
            Integer pageSize,
            @RequestParam(required = true)
            String keyword
    )
    {
        Page<Admin> page = new Page<>(pageNum, pageSize);
        adminService.searchAdmin(page,keyword, SearchLogic.OR,"nickname","real_name");
        return SaResult.ok().setData(page);
    }
    /**
     * 根据username搜索
     * @param pageNum
     * @param pageSize
     * @param keyword 搜索关键词
     * @return
     */
    @SaCheckLogin
    @GetMapping("/search/by_username")
    @SaCheckPermission("admin")
    public SaResult searchAdminByUsername(
            @RequestParam(required = false,defaultValue = "0")
                    Integer pageNum,
            @RequestParam(required = false,defaultValue = "5")
                    Integer pageSize,
            @RequestParam(required = true)
                    String keyword
    )
    {
        Page<Admin> page = new Page<>(pageNum, pageSize);
        adminService.searchAdmin(page,keyword, SearchLogic.OR,"username");
        return SaResult.ok().setData(page);
    }

}
