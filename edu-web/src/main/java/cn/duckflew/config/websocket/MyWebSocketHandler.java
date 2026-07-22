package cn.duckflew.config.websocket;

import cn.duckflew.entity.Dictionary;
import cn.duckflew.mapper.DictionaryMapper;
import cn.duckflew.service.DictionaryService;
import cn.duckflew.utils.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理 WebSocket连接
 */
@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {

    /**
     * 固定前缀
     */
    private static final String USER_ID = "user_id_";

    /**
     * 存放Session集合，方便推送消息
     */
    private static ConcurrentHashMap<String, WebSocketSession> webSocketSessionMaps = new ConcurrentHashMap<>();

    // 监听：连接开启
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        // put到集合，方便后续操作
        String userId = session.getAttributes().get("userId").toString();
        webSocketSessionMaps.put(USER_ID + userId, session);
        // 给个提示
        log.info("用户:{}建立ws连接,tokenValue={}",userId,session.getId());
        sendMessage(session,"连接成功");
        flushOnlineNum();
    }

    // 监听：连接关闭
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 从集合移除
        String userId = session.getAttributes().get("userId").toString();
        webSocketSessionMaps.remove(USER_ID + userId);
        log.info("用户:{}断开连接 token:{}",userId,session.getId());
        flushOnlineNum();
    }

    // 收到消息
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException
    {
        log.debug("sid为{},发来消息:{}", session.getId() , message);
    }

    // -----------

    // 向指定客户端推送消息
    public static void sendMessage(WebSocketSession session, String message) {
        try {
            log.debug("向sessionId={}的user发送了消息:{}",session.getId(),message);
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 向指定用户推送消息
    public static void sendMessage(Integer userId, String message) {
        WebSocketSession session = webSocketSessionMaps.get(USER_ID + userId);
        if(session != null) {
            sendMessage(session, message);
        }
    }

    /**
     * 获取用户是否在线
     */
    public static boolean isOnline(Integer userId)
    {
        return webSocketSessionMaps.get(USER_ID + userId)!=null;
    }

    public  void flushOnlineNum()
    {
        DictionaryService dictionaryService = (DictionaryService) SpringUtil.getBean("dictionaryService");
        Dictionary onlineNum = dictionaryService.getOne(new QueryWrapper<Dictionary>().eq("dic_name", "onlineNum"));
        if (onlineNum==null)
        {
            onlineNum = new Dictionary();
            onlineNum.setDicName("onlineNum");
        }
        onlineNum.setDicValue(String.valueOf(webSocketSessionMaps.size()));
        onlineNum.setDicValueType(1);
        dictionaryService.saveOrUpdate(onlineNum);
    }
}
