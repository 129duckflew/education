package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.Dictionary;
import cn.duckflew.entity.admin.Admin;
import cn.duckflew.entity.admin.Menu;
import cn.duckflew.service.AnswerService;
import cn.duckflew.service.DictionaryService;
import cn.duckflew.service.LoginLogService;
import cn.duckflew.service.QuestionService;
import cn.duckflew.service.admin.AdminService;
import cn.duckflew.service.admin.MenuService;
import cn.duckflew.validate.group.UpdateSelf;
import cn.duckflew.vo.AdminLoginParams;
import cn.duckflew.vo.LoginParams;
import cn.duckflew.vo.admin.AdminVo;
import cn.duckflew.vo.admin.MenuVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 管理员主界面相关接口
 */
@RestController
@RequestMapping("/home")
@Slf4j
public class AdminHomeController
{
    @Autowired
    AdminService adminService;
    @Autowired
    MenuService menuService;
    /**
     * 获取当前管理员用户的菜单列表
     * @return
     */
    @SaCheckLogin
    @GetMapping("/menu")
    public SaResult getAdminMenuList()
    {
        Integer adminId = StpUtil.getLoginIdAsInt();
        List<Menu> menuList=adminService.adminMenuList(adminId);
        List<MenuVo> res = menuService.menuListToVoList(menuList);
        return SaResult.ok().setData(res);
    }


    /**
     * 获取自己的个人资料
     */
    @GetMapping("/profile")
    @SaCheckLogin
    public SaResult profile()
    {
        Integer adminId = StpUtil.getLoginIdAsInt();
        Admin adminProfile = adminService.getById(adminId);
        adminProfile.setPassword(null);
        return SaResult.ok().setData(adminProfile);
    }
    /**
     * 修改自己的个人资料
     * @param adminVo
     * @return
     */
    @PutMapping("/profile")
    @SaCheckLogin
    public SaResult updateProfile(@RequestBody  @Validated({UpdateSelf.class}) AdminVo adminVo)
    {
        Integer adminId = StpUtil.getLoginIdAsInt();
        adminVo.setId(adminId);
        adminService.updateAdmin(adminVo);
        return SaResult.ok();
    }

    /**
     * 后台登录 登录之后返回token
     * @param adminLoginParam 管理员登录需要的参数
     * @return
     * @apiNote 后台登录只需要账号和密码 其他的信息不需要
     * @response {
     *     "code": 200,
     *     "msg": "ok",
     *     "data": {
     *         "tokenName": "satoken",
     *         "tokenValue": "99c39726-3324-4204-9449-4dff06510090",
     *         "isLogin": true,
     *         "loginId": "1",
     *         "loginType": "login",
     *         "tokenTimeout": 2592000,
     *         "sessionTimeout": 2592000,
     *         "tokenSessionTimeout": -2,
     *         "tokenActivityTimeout": -1,
     *         "loginDevice": "default-device",
     *         "tag": null
     *     }
     * }
     */
    @PostMapping("/login")
    public SaResult login(@Validated @RequestBody AdminLoginParams adminLoginParam)
    {
        log.info("管理员登录:{}", adminLoginParam);
        return adminService.login(adminLoginParam.getUsername(),adminLoginParam.getPassword());
    }

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    DictionaryService dictionaryService;
    /**
     * 获取当前在线用户数
     * @return
     */
    @GetMapping("/online")
    public SaResult getOnlineNum()
    {
        Dictionary dic = dictionaryService.getOne(new QueryWrapper<Dictionary>().eq("dic_name", "onlineNum"));
        if (dic==null)
        return SaResult.ok().setMsg("暂无数据");
        return SaResult.ok().setData(Integer.parseInt(dic.getDicValue()));
    }


    @Autowired
    LoginLogService loginLogService;
    /**
     * 获取时间段内的活跃数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * @apiNote 时间传递格式 :yyyy-MM-dd
     */
    @GetMapping("/active")
    public SaResult getActiveNum(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date endTime
    )
    {
        Integer num=loginLogService.getActiveNum(startTime,endTime);
        return SaResult.ok().setData(num);
    }

    @Autowired
    QuestionService questionService;

    /**
     * 获取时间段内的提问数量
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/question/count")
    public SaResult getQuestionNum(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date endTime
    )
    {
        Integer num=questionService.getQuestionNum(startTime,endTime);
        return SaResult.ok().setData(num);
    }


    @Autowired
    AnswerService answerService;
    /**
     * 获取时间段内的回答数量
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/answer/count")
    public SaResult getAnsNum(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date endTime
    )
    {
        Integer num= answerService.getAnsNum(startTime,endTime);
        return SaResult.ok().setData(num);
    }
}
