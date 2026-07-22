package cn.duckflew.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;


@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterParamsMailCheck
{


    /**
     * 用户名
     */
    @NotBlank(message = "新用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$",message = "请输入6~20位大小写字母或者数字及它们的组合!")
    private String username;
    /**
     * 密码
     */
    @NotBlank
    private String password;
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    /**
     * 邮箱验证码
     */
    @NotBlank
    @Length(min = 6,max = 6,message = "请输入6位验证码")
    private String emailCheckCode;

    /**
     * 昵称
     */
    private String nickName;
    /**
     * 性别
     */
    @Length(min = 1,max = 1,message = "请输入'男'或'女'")
    private String gender;
    /**
     * 出生日期 格式 yyyy-MM-dd
     */
    @Past(message = "请输入过去的时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
}
