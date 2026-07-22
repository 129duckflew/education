package cn.duckflew.service;

import cn.duckflew.entity.system.ChatMsg;
import cn.duckflew.mapper.ChatMsgMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ChatMsgService extends ServiceImpl<ChatMsgMapper, ChatMsg>
{
}
