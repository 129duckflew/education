package cn.duckflew.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("t_user_op")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOp
{

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 操作类型
     */
    private Integer opType;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 资源id
     */
    private Integer resourceId;
    /**
     * 操作状态
     */
    private Integer opStatus;
}
