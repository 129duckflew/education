package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginParams {

    /**
     * 登录类型  1:邮箱登录,2:手机号登录,3:用户名密码登录,4:手机验证码登录;
     */
    @NotNull(message = "登录类型必须指定")
    int loginType;
    /**
     * 用户名
     * @since 如果是与用户名相关的登录 那就是必须的
     */
    String username;
    /**
     * 密码
     * @since  用到了密码就是必须的
     */
    String password;
    /**
     * 邮箱
     * @since 用到了邮箱就是必须的
     */
    @Email(message = "邮箱格式不正确")
    String email;
    /**
     * 手机号
     * @since 用到了手机号就必须的
     */
    String telephoneNumber;
    /**
     * 邮箱/手机   验证码
     * @since 具体是什么验证码根据登录类型的不同区分
     */
    @Length(min = 6,max = 6,message = "验证码必须为6位")
    String checkCode;

}
