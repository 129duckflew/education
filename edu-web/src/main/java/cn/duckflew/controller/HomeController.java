package cn.duckflew.controller;


import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.enums.RabbitMailType;
import cn.duckflew.utils.StringUtil;
import cn.duckflew.vo.*;
import cn.duckflew.service.BaseUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 主界面相关接口
 */
@RestController
@Slf4j
@Validated
public class HomeController {


    @Autowired
    BaseUserService baseUserService;

    /**
     * 前台用户登录
     * @param loginParams 登录所需的参数
     * @return
     * @response {
     *     "code": 200,
     *     "msg": "登录成功",
     *     "data": {
     *         "tokenName": "satoken",
     *         "tokenValue": "3728262f-a8a6-4b8d-98f2-7fb9225ac2a4",
     *         "isLogin": true,
     *         "loginId": "5",
     *         "loginType": "login",
     *         "tokenTimeout": 2592000,
     *         "sessionTimeout": 2592000,
     *         "tokenSessionTimeout": -2,
     *         "tokenActivityTimeout": -1,
     *         "loginDevice": "default-device",
     *         "tag": nullq
     *     }
     * }
     * @apiNote Token的使用方法 TokenName是Token的参数名，TokenValue是Token的值 Token写在请求头中,有Token并且校验通过的认为已经登录
     *
     */
    @PostMapping("/home/login")
    public SaResult homeLogin(@RequestBody LoginParams loginParams) {
        return baseUserService.homeLogin(loginParams);
    }

    /**
     *  邮箱验证注册成为咨询者
     * @param registerParamsMailCheck 注册需要的参数
     * @return
     * @response {
     *     "code": 200,
     *     "msg": "注册成功",
     *     "data": null
     * }
     */
    @PostMapping("/home/register")
    public SaResult homeRegister(@Validated @RequestBody RegisterParamsMailCheck registerParamsMailCheck) {
        return baseUserService.registerBaseUser(registerParamsMailCheck);
    }

    /**
     *  手机验证码注册
     * @param registerParamsTextMsgCheck 手机号注册需要的参数
     * @return
     * @response {
     *     "code": 200,
     *     "msg": "注册成功! 请登录后尽快完善个人信息",
     *     "data": null
     * }
     */
    @PostMapping("/home/register/text_msg_check")
    public SaResult homeRegisterTextMsgCheck(@Validated @RequestBody RegisterParamsTextMsgCheck registerParamsTextMsgCheck)
    {
        return baseUserService.registerBaseUserByTextMsg(registerParamsTextMsgCheck);
    }

    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 获取绑定/注册邮箱验证码
     * @param email 邮箱
     * @return
     * @response {
     *     "code": 200,
     *     "msg": "验证码已经发送到邮箱,请注意查收,验证码有效期90s",
     *     "data": null
     * }
     */
    @GetMapping("/register/email_check_code")
    public SaResult registerEmailCheckCode(
            @RequestParam(value = "email")
            @Email(message = "邮箱格式不正确")
            @NotBlank(message = "邮箱不能为空")
            String email)
    {
        BaseUser user = baseUserService.getOne(new QueryWrapper<BaseUser>().eq("email", email));
        if (user!=null) {
            return SaResult.error().setMsg("此邮箱已经被注册");
        }
        ValueOperations<String,Object> ops = redisTemplate.opsForValue();
        String redisEmailCode = (String) ops.get(email);
        log.warn("redis中不存在此Email的Code,email:{}",email);
        if (!StringUtil.isNullOrEmpty(redisEmailCode)) {
            return SaResult.error().setMsg("操作频率太高了  请稍后再试");
        }
        String randomCode = StringUtil.getRandomCode(6);
        ops.set(email,randomCode, Duration.ofSeconds(90));
        // send email
        Map<String,Object> mailData=new HashMap<>();
        mailData.put("email",email);
        mailData.put("checkCode",randomCode);
        mailData.put("mailTypeCode", RabbitMailType.REGISTER_MAIL.getCode());
        rabbitTemplate.convertAndSend("mail.email_check",mailData);
        log.info("推送邮件请求到消息队列 收件人为{}",email);
        log.info("邮件验证码为{}",randomCode);
        return SaResult.ok().setMsg("验证码已经发送到邮箱,请注意查收,验证码有效期90s");
    }

    /**
     * 获取邮箱是否被绑定
     * @return
     */
    @GetMapping("/mail_exist")
    public SaResult getMailExist(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确")
                                 String email)
    {
        log.debug("mail exist?{}",email);
        return baseUserService.mailExist(email);
    }

    /**
     * 获取短信验证码
     * @param phoneNumber 手机号码
     * @return
     * @response {
     *     "code": 200,
     *     "msg": "验证码已经发送到您的手机,请注意查收,验证码有效期90s",
     *     "data": null
     * }
     */

    @GetMapping("/msg_check_code")
    public SaResult textMsgCheckCode(
            @Length(min = 11,max = 11,message = "手机号码格式不正确")
            @NotBlank(message = "手机号不能为空")
                    String phoneNumber
    )
    {
        ValueOperations<String,Object> ops = redisTemplate.opsForValue();
        if (!StringUtil.isNullOrEmpty((String) ops.get(phoneNumber))) {
            return SaResult.error().setCode(400).setMsg("操作频率太高了  请稍后再试");
        }
        log.info(phoneNumber+"的手机号验证码:"+(String) ops.get(phoneNumber));
        String randomCode = StringUtil.getRandomDigitCode(6);
        ops.set(phoneNumber,randomCode, Duration.ofSeconds(90));
        // send phone check code
        Map<String,String> phoneCheckCodeData=new HashMap<>();
        phoneCheckCodeData.put("phoneNumber",phoneNumber);
        phoneCheckCodeData.put("checkCode",randomCode);
        rabbitTemplate.convertAndSend("msg.text_msg_check",phoneCheckCodeData);
        log.info("推送手机验证码请求到消息队列 接收人为{},验证码是{}",phoneNumber,randomCode);
        return SaResult.ok().setMsg("验证码已经发送到您的手机,请注意查收,验证码有效期90s");
    }

}
