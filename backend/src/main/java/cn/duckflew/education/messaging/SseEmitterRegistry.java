package cn.duckflew.education.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进程内 SSE 连接注册表。用于实时推送通知（替代旧版 WebSocket 在线表）。
 */
@Slf4j
@Component
public class SseEmitterRegistry {

    private static final long NO_TIMEOUT = 0L;
    private static final long HEARTBEAT_MS = 25_000L;

    private final Map<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(Long userId) {
        SseEmitter emitter = new SseEmitter(NO_TIMEOUT);
        Set<SseEmitter> set = emitters.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet());
        set.add(emitter);
        Runnable cleanup = () -> {
            set.remove(emitter);
            if (set.isEmpty()) {
                emitters.remove(userId, set);
            }
        };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(error -> cleanup.run());
        send(emitter, "connected", "ok");
        return emitter;
    }

    public void publish(Long userId, String eventName, Object data) {
        Set<SseEmitter> set = emitters.get(userId);
        if (set == null) {
            return;
        }
        for (SseEmitter emitter : set) {
            send(emitter, eventName, data);
        }
    }

    @Scheduled(fixedDelay = HEARTBEAT_MS)
    public void heartbeat() {
        emitters.forEach((userId, set) -> {
            for (SseEmitter emitter : set) {
                send(emitter, "ping", "1");
            }
        });
    }

    private void send(SseEmitter emitter, String name, Object data) {
        try {
            emitter.send(SseEmitter.event().name(name).data(data));
        } catch (IOException | IllegalStateException ex) {
            emitter.complete();
        }
    }
}
