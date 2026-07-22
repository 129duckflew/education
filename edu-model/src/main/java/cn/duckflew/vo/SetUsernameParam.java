package cn.duckflew.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
public class SetUsernameParam
{
    /**
     * 新设置的用户名
     */
    @NotBlank(message = "新用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$",message = "请输入6~20位大小写字母或者数字及它们的组合!")
    private String username;
}
