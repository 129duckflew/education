package cn.duckflew.education.messaging;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.security.JwtService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 统一实时流：通知 / 私信消息 / 已读回执共用一条 SSE 连接。
 * EventSource 无法自定义请求头，故用 query 参数传 token。
 */
@RestController
public class StreamController {

    private final JwtService jwtService;
    private final SseEmitterRegistry sseEmitterRegistry;

    public StreamController(JwtService jwtService, SseEmitterRegistry sseEmitterRegistry) {
        this.jwtService = jwtService;
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @GetMapping(value = "/api/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam("token") String token) {
        return register(token);
    }

    /** 兼容旧路径。 */
    @GetMapping(value = "/api/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter legacyStream(@RequestParam("token") String token) {
        return register(token);
    }

    private SseEmitter register(String token) {
        Long userId;
        try {
            userId = jwtService.userIdFromAccessToken(token);
        } catch (RuntimeException ex) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return sseEmitterRegistry.register(userId);
    }
}
