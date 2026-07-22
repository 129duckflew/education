package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestPwdParam
{

    /**
     * 新密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 6,max = 18,message = "请输入长度为6-18位的字符组合")
    private String password;

    /**
     * 手机号验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "\\d{6}",message = "请输入6位验证码")
    private String checkCode;
}
