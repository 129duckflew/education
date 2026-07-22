package cn.duckflew.config.websocket;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手的前置拦截器
 */
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor
{

    // 握手之前触发 (return true 才会握手成功 )
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler,
                                   Map<String, Object> attr) {

        log.debug("用户尝试创建连接,token={}",StpUtil.getTokenValue());
        // 未登录情况下拒绝握手
        if(!StpUtil.isLogin()) {
            log.error("token无效,ws连接失败");
            return false;
        }

        // 标记 userId，握手成功
        attr.put("userId", StpUtil.getLoginIdAsLong());
        return true;
    }

    // 握手之后触发
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
        log.debug("创建连接完成的回调");
    }

}

