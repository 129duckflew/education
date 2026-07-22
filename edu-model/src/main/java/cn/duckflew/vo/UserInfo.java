package cn.duckflew.vo;

import cn.duckflew.entity.ProInfo;
import cn.duckflew.entity.user.BaseUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo
{
    /**
     * 基础信息
     */
   BaseUser basicInfo;
    /**
     * 专家信息
     */
   ProInfo proInfo;
}
