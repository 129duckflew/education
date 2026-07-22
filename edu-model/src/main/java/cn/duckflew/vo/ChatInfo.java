package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatInfo
{
    private Integer toUserId;
    private String  toUserAvatar;
    private String  toUserName;
    private Integer notReadNum;
}
