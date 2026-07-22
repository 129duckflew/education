package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatParam
{
    /**
     * 发送给谁
     */
    @NotNull(message = "接受者用户id不能为空")
    private Integer userId;

    /**
     * 消息内容
     */
    @NotEmpty(message = "消息内容不能为空")
    private String msgContent;
}
