package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.service.BaseUserService;
import cn.duckflew.validate.group.UpdateGroup;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理:用户相关接口
 */
@RestController
@RequestMapping("/user")
@Validated
public class BaseUserController
{

    @Autowired
    BaseUserService baseUserService;


    /**
     * 禁用账户
     * @param ids
     * @return
     */
    @PostMapping("/ban")
    @SaCheckLogin
    @SaCheckPermission("user")
    public SaResult banBaseUser(
            @NotEmpty(message = "id数组不能为空")
            int []ids
    )
    {
        List<Integer> userIds = Arrays.stream(ids).boxed().collect(Collectors.toList());
        baseUserService.banBaseUser(userIds);
        return SaResult.ok().setMsg("操作成功");
    }


    /**
     * 搜索用户
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     * @response {
     *     "code": 200,
     *     "msg": "",
     *     "data":{
     *         "total": 0,
     *         "userList":[]
     *     }
     * }
     * @apiNote 三个参数都可以为空 如果搜索词为空 则只分页
     */
    @GetMapping("/")
    @SaCheckLogin
    @SaCheckPermission("user")
    public SaResult searchUser(
            @RequestParam(required = false,defaultValue = "0") Integer pageNum,
            @RequestParam(required = false,defaultValue = "5") Integer pageSize,
            @RequestParam(required = false,defaultValue = "")String keyword
    )
    {
        IPage<BaseUser> page= new Page<>(pageNum,pageSize);
        if (keyword!=null&&!keyword.isEmpty())
        {
            baseUserService.page(page,new QueryWrapper<BaseUser>()
            .like("nick_name","%"+keyword+"%")
            .or().like("real_name","%"+keyword+"%"));
        }
        else
        {
            baseUserService.page(page);
        }
        return SaResult.ok().setData(page);
    }


    /**
     * 修改用户信息
     * @param baseUser
     * @return
     */
    @PutMapping("/")
    @SaCheckPermission("user")
    @SaCheckLogin
    public SaResult updateUser(
            @RequestBody @Validated({UpdateGroup.class}) BaseUser baseUser
    )
    {
        baseUserService.updateUser(baseUser);
        return SaResult.ok().setMsg("修改成功");
    }

}
