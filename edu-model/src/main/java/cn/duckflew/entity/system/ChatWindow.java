package cn.duckflew.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("chat_window")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatWindow
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer fromUserId;
    private Integer toUserId;
}
