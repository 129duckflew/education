package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterParamsTextMsgCheck
{
    @NotBlank(message = "电话号码不能为空")
    @Length(min = 11 ,max = 11,message = "电话号码格式不正确")
    /**
     *  电话号码
     *  @since  目前只支持国内的号码 11位
     */
    private String phoneNumber;

    @NotBlank(message = "验证码不能为空")
    @Length(min = 6 ,max = 6,message = "验证码格式不正确")
    /**
     *  手机验证码
     *  @since  6位验证码
     */
    private String checkCode;



    /**
     * 用户名
     * @since 用户名只能是6~20位大小写字母或者数字及它们的组合
     */
    @NotBlank(message = "新用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$",message = "用户名格式错误,请输入6~20位大小写字母或者数字及它们的组合!")
    private String username;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;


}
