package cn.duckflew.service;

import cn.duckflew.entity.system.ChatWindow;
import cn.duckflew.mapper.ChatWindowMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ChatWindowService extends ServiceImpl<ChatWindowMapper,ChatWindow>
{
    @Transactional(rollbackFor =Exception.class)
    public void addWindow(Integer fromUserId, Integer toUserId)
    {
        ChatWindow exist = getOne(new QueryWrapper<ChatWindow>().eq("from_user_id", fromUserId).eq("to_user_id", toUserId));
        if (exist==null)
        {
            ChatWindow cw = new ChatWindow();
            cw.setFromUserId(fromUserId);
            cw.setToUserId(toUserId);
            save(cw);
            log.warn("添加新的聊天对象 fromUserId:{},toUserId{}",fromUserId,toUserId);
        }
        else
        {
            log.warn("添加新的聊天对象发现已经存在 fromUserId:{},toUserId{}",fromUserId,toUserId);
        }
    }


}
