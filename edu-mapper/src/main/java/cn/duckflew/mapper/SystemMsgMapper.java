package cn.duckflew.mapper;

import cn.duckflew.entity.system.SystemMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
public interface SystemMsgMapper extends BaseMapper<SystemMsg>
{

    List<Integer> getChatIdList(Integer userId);

    List<SystemMsg> getRecordList(@Param("fromUserId") Integer fromUserId, @Param("toUserId") Integer toUserId);
}
