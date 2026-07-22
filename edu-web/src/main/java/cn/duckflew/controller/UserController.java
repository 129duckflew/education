package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.config.websocket.MyWebSocketHandler;
import cn.duckflew.entity.LoginLog;
import cn.duckflew.entity.ProInfo;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.exception.FileUploadException;
import cn.duckflew.service.*;
import cn.duckflew.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.Map;

/**
 * 用户相关接口
 * @author duckflew
 */
@RestController
@Validated
@RequestMapping("/user")
@Slf4j
public class UserController
{
    @Autowired
    BaseUserService baseUserService;


    @Autowired
    ProfessorService professorService;

    /**
     * 获取个人信息
     * @return
     * @response {
     *     "code": 200,
     *     "msg": "ok",
     *     "data": {
     *         "userInfo": {
     *             "id": 12,
     *             "telephoneNumber": null,
     *             "username": "hehong",
     *             "password": null,
     *             "email": "3072755214@qq.com",
     *             "birthday": "1970-08-02",
     *             "gender": "女",
     *             "nickName": "贺红",
     *             "avatar": null,
     *             "cardId": "422324196906092431",
     *             "userType": 3,
     *             "realName": "贺红",
     *             "jobRankId": 2,
     *             "cvFileName": "26865759-c2e8-4c95-9c70-b170a912d12f.pdf"
     *         },
     *         "areaNames": [
     *             "计算机类",
     *             "计算机科学与技术",
     *             "电气工程",
     *             "软件工程",
     *             "计算机软件理论"
     *         ]
     *     }
     * }
     */
    @SaCheckLogin
    @GetMapping("/profile/{userId}")
    public SaResult profile(@PathVariable Integer userId)
    {
        int loginUserId = StpUtil.getLoginIdAsInt();
        Map<String,Object> res=baseUserService.getProfileById(loginUserId,userId);
        return SaResult.ok().setData(res);
    }

    @Autowired
    EsService esService;
    /**
     * 更改头像信息
     * @param fileId 文件id
     * @return
     */
    @PutMapping("/profile/avatar")
    @SaCheckLogin
    public SaResult setAvatar(
            @RequestParam("fileId")
            @NotBlank(message = "文件id不能为空")
            String fileId
    )
    {
        BaseUser user = baseUserService.getById(StpUtil.getLoginIdAsInt());
        user.setAvatar(fileId);
        baseUserService.updateById(user);
        if (baseUserService.isPro(user.getId()))
        {
            esService.saveProfessorInfoDoc(professorService.getProfessorDoc(user));
        }
        return SaResult.ok().setMsg("设置头像成功");
    }
    @Autowired
    ProInfoService proInfoService;
    /**
     * 上传简历信息
     * @param fileId 文件id
     * @return
     */
    @PostMapping("/profile/cv")
    @SaCheckLogin
    public SaResult setCV(
            @NotBlank(message = "文件id不能为空")
                    String fileId
    )
    {
        int userId = StpUtil.getLoginIdAsInt();
        ProInfo proInfo = proInfoService.getById(userId);
        if (proInfo==null)
        {
            proInfo=new ProInfo();
            proInfo.setId(userId);
        }
        proInfo.setCvFile(fileId);
        proInfoService.saveOrUpdate(proInfo );
        return SaResult.ok().setMsg("上传简历附件信息成功");
    }


    /**
     * 申请成为教授
     * @return
     * @apiNote 必须先登录
     * @response {
     *     "code": 200,
     *     "msg": "申请成功,请等待审核",
     *     "data": null
     * }
     */
    @PostMapping("/apply_pro")
    @SaCheckLogin
    public SaResult requestToBeProfessor()
    {
        Integer userId=StpUtil.getLoginIdAsInt();
        baseUserService.requestToBeProfessor(userId);
        return SaResult.ok().setMsg("申请成功,请等待审核");
    }

    /**
     * 用户选择感兴趣的领域
     */
    @SaCheckLogin
    @PostMapping("/area/like")
    public SaResult addInterest(@RequestBody @Validated ConsultAreaIds consultAreaIds)
    {
        int userId = StpUtil.getLoginIdAsInt();
        baseUserService.addInterestArea(userId, consultAreaIds.getAreaIds());
        return SaResult.ok().setMsg("添加领域成功");
    }

    /**
     * 邮箱绑定
     * @return
     */
    @PostMapping("/profile/bind_mail")
    public SaResult bindMail(@Validated @RequestBody BindMailParams bindMailParams)
    {
        int userId = StpUtil.getLoginIdAsInt();
        return  baseUserService.bindMail(userId,bindMailParams);
    }

    /**
     * 设置真名
     * @param realName 真名
     * @return
     */
    @PostMapping("/profile/real_name")
    @SaCheckLogin
    public SaResult setRealName(
            @NotBlank(message = "真名不能为空")
            @Pattern(regexp = "^[\\u4E00-\\u9FA5]{2,4}$",message = "请输入真实姓名")
            @RequestParam("realName") String realName
    )
    {
        int userId = StpUtil.getLoginIdAsInt();
        baseUserService.setRealName(userId,realName);
        return SaResult.ok().setMsg("设置成功");
    }

    /**
     * 设置身份证号
     * @param cardId 身份证号
     * @return
     * @apiNote 请求方式是 form-data
     */
    @PostMapping("/profile/card_id")
    public  SaResult setCardId(
            @NotBlank(message = "身份证号不能为空")
            @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",message = "请输入正确格式的身份证号")
            @RequestParam("cardId")String cardId
    )
    {
        int userId = StpUtil.getLoginIdAsInt();
        baseUserService.setCardId(userId,cardId);
        return SaResult.ok().setMsg("设置成功");
    }


    /**
     * 设置用户名
     * @param setUsernameParam 新设置的用户名
     */
    @PostMapping("/profile/username")
    public SaResult setUsername(@Validated @RequestBody SetUsernameParam setUsernameParam)
    {

        BaseUser check = baseUserService.getOne(new QueryWrapper<BaseUser>().eq("username", setUsernameParam.getUsername()));
        if (check!=null)
            return SaResult.error().setCode(400).setMsg("此用户名已经被注册");
        BaseUser baseUser = new BaseUser();
        baseUser.setId(StpUtil.getLoginIdAsInt());
        baseUser.setUsername(setUsernameParam.getUsername());
        baseUserService.updateById(baseUser);
        return SaResult.ok().setMsg("设置成功");
    }


    /**
     * 修改性别
     * @param gender 男/女
     * @return
     * @apiNote 注意传参方式是 form-data 不是 json
     */
    @PostMapping("/profile/gender")
    @SaCheckLogin
    public SaResult setGender(
            @NotBlank(message = "性别不能为空")
            @Pattern(regexp = "^[男女]$",message = "性别只能为男或女")
            @RequestParam("gender") String gender
    )
    {
        int userId = StpUtil.getLoginIdAsInt();
        baseUserService.setUserGender(gender,userId);
        return SaResult.ok().setMsg("设置成功");
    }

    /**
     * 绑定手机号
     * @param phoneNumber 电话号码
     * @param checkCode 验证码
     * @return
     * @apiNote 注意请求方式不是json 是form-data
     */
    @PostMapping("/profile/bind_phone")
    public SaResult setPhone(
            @NotBlank(message = "手机号不能为空")
            @Pattern(regexp = "^1[3456789]\\d{9}$",message = "手机号格式不正确")
            @RequestParam("phoneNumber") String phoneNumber,
            @NotBlank(message = "验证码不能为空")
            @Pattern(regexp = "\\d{6}",message = "验证码必须为6位数字" )
            @RequestParam("checkCode") String checkCode
    )
    {
        Integer userId = StpUtil.getLoginIdAsInt();
        baseUserService.setPhone(userId,phoneNumber,checkCode );
        return SaResult.ok().setMsg("设置成功");
    }


    /**
     * 查询用户是否在线
     * @param userId 用户id
     * @return
     */
    @GetMapping("/online/{userId}")
    public SaResult isOnline(@NotNull(message = "用户id不能为空")@PathVariable Integer userId)
    {
        boolean online = MyWebSocketHandler.isOnline(userId);
        return SaResult.ok().setData(online);
    }

    /**
     * 根据用户id获取头像
     * @param userId
     * @return
     */
    @GetMapping("/avatar/{userId}")
    public SaResult getAvatarById(
            @NotNull(message = "用户id不能为空")
            @PathVariable Integer userId)
    {
        BaseUser user = baseUserService.getById(userId);
        return SaResult.ok().setData(user.getAvatar());
    }

    /**
     * 重置密码
     * @return
     * @apiNote 重置密码需要获取手机号验证码
     */
    @PostMapping("/profile/pwd_reset")
    @SaCheckLogin
    public SaResult restPwd(@Validated @RequestBody RestPwdParam restPwdParam)
    {
        int userId = StpUtil.getLoginIdAsInt();
        baseUserService.resetPwd(userId,restPwdParam.getCheckCode(),restPwdParam.getPassword());
        return SaResult.ok().setMsg("修改成功");
    }


    @Autowired
    LoginLogService loginLogService;

    /**
     * 获取用户最后一次登录的记录 如果为空则是第一次登录系统
     * @return
     */
    @GetMapping("/last_login")
    @SaCheckLogin
    public SaResult getLastLoginInfo()
    {
        int userId = StpUtil.getLoginIdAsInt();
        LoginLog info = loginLogService.lastLoginInfo(userId);
        return SaResult.ok().setData(info);
    }
}
