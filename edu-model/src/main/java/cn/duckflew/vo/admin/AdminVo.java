package cn.duckflew.vo.admin;

import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import cn.duckflew.validate.group.UpdateSelf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminVo
{
    /**
     * id
     */
    @NotNull(groups = UpdateGroup.class,message = "必须指定id")
    @Null(groups = AddGroup.class,message = "新增时不能指定id")
    @Null(groups = UpdateSelf.class,message = "修改个人信息不能指定id")
    private Integer id;

    /**
     * 是否启用
     */
    @Null(groups = AddGroup.class,message = "新建用户时默认启用")
    private Boolean enabled;

    /**
     * 昵称
     */
    @NotNull(groups = AddGroup.class,message = "必须指定昵称")
    private String nickname;
    /**
     * 真名
     */
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z\\s]+(·[\\u4E00-\\u9FA5A-Za-z]+)*$",message = "姓名不符合规范")
    @NotNull(groups = AddGroup.class,message = "必须指定真名")
    private String realName;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$",message = "手机号格式不正确")
    @NotNull(groups = AddGroup.class,message = "必须指定手机号")
    private String telephone;


    /**
     * 用户名
     */
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$",message = "请输入6~20位大小写字母或者数字及它们的组合!")
    @NotNull(groups = AddGroup.class,message = "必须指定用户名")
    private String username;

    /**
     * 密码
     */
    @NotBlank(groups = AddGroup.class,message = "密码不能为空")
    private String password;

    /**
     * 用户头像
     */
    private String avatar;
}
