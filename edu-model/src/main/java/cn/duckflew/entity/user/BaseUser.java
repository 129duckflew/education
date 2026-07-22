package cn.duckflew.entity.user;

import cn.duckflew.validate.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class BaseUser implements Serializable
{

    /**
     * 用户id 不需要传递
     *
     */
    @NotNull(message = "更新必须指定id ",groups = UpdateGroup.class)
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 手机号
     */
    private String telephoneNumber;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱 必须
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 出生日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthday;
    /**
     * 性别
     */
    private String gender;
    private String nickName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 身份证号
     */
    @Null(message = "不允许修改这个字段",groups = UpdateGroup.class)
    private String cardId;

    /**
     * 用户类型
     */
    @Null(message = "不允许修改这个字段",groups = UpdateGroup.class)
    private Integer   userType;

    /**
     * 真名
     */
    private String realName;

    /**
     * 是否启用
     */
    @Null(message = "不允许修改这个字段",groups = UpdateGroup.class)
    private Boolean enabled;


    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date createTime;

}
