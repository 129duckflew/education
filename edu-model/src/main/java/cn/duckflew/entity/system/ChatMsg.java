package cn.duckflew.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("chat_msg")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsg
{
    private Integer id;
    private String chatMsgContent;
}
