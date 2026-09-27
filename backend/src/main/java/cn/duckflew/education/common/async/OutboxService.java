package cn.duckflew.education.common.async;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 事务内写入 outbox 任务，事务提交后由 {@link OutboxProcessor} 异步执行。
 */
@Service
public class OutboxService {

    private final OutboxTaskRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxTaskRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void enqueue(String type, Object payload) {
        OutboxTask task = new OutboxTask();
        task.setType(type);
        task.setPayload(writeJson(payload));
        repository.save(task);
    }

    private String writeJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JacksonException e) {
            throw new IllegalStateException("outbox payload 序列化失败", e);
        }
    }
}
