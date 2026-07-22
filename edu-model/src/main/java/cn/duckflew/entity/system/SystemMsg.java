package cn.duckflew.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@TableName("t_sys_msg")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemMsg
{
    /**
     * 消息id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 是否已读
     * @since 0为未读 1为已读
     */
    private Integer isRead;
    /**
     * 消息类型
     * @since 0为系统消息  1为私信 2为点赞消息 3为收藏消息
     */
    private Integer msgType;
    /**
     * 消息接收用户
     */
    private Integer toUserId;
    /**
     * 消息发送用户
     */
    private Integer fromUserId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "Asia/Shanghai")
    private Date createTime;

    /**
     * 相关资源的id
     */
    private  Integer resourceId;
    /**
     * 相关用户的id
     */
    private Integer relatedUserId;



    public static SystemMsg baseMsg()
    {
        SystemMsg systemMsg = new SystemMsg();
        systemMsg.setIsRead(0);
        systemMsg.setCreateTime(new Date());
        return systemMsg;
    }
}
