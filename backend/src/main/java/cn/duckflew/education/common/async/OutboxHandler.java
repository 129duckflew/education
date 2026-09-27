package cn.duckflew.education.common.async;

/**
 * outbox 任务处理器，按 {@link #type()} 注册。
 */
public interface OutboxHandler {

    String type();

    void handle(String payloadJson) throws Exception;
}
