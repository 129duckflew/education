package cn.duckflew.education.common.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 轮询 outbox 并分发。失败按指数退避重试，超过上限置为 FAILED。
 * 以虚拟线程执行（spring.threads.virtual.enabled=true 时 @Scheduled 亦使用虚拟线程）。
 */
@Slf4j
@Component
public class OutboxProcessor {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BATCH = 20;

    private final OutboxTaskRepository repository;
    private final Map<String, OutboxHandler> handlers;

    public OutboxProcessor(OutboxTaskRepository repository, List<OutboxHandler> handlerList) {
        this.repository = repository;
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(OutboxHandler::type, Function.identity()));
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval:5000}")
    @Transactional
    public void process() {
        List<OutboxTask> tasks = repository.findByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
                OutboxTask.Status.PENDING, Instant.now(), PageRequest.of(0, BATCH));
        for (OutboxTask task : tasks) {
            dispatch(task);
        }
    }

    private void dispatch(OutboxTask task) {
        OutboxHandler handler = handlers.get(task.getType());
        if (handler == null) {
            fail(task, "未注册的 outbox 类型: " + task.getType());
            return;
        }
        try {
            handler.handle(task.getPayload());
            task.setStatus(OutboxTask.Status.DONE);
            task.setCompletedAt(Instant.now());
        } catch (Exception ex) {
            log.warn("outbox 任务 {} 执行失败", task.getId(), ex);
            fail(task, ex.getMessage());
        }
        repository.save(task);
    }

    private void fail(OutboxTask task, String error) {
        task.setAttempts(task.getAttempts() + 1);
        task.setLastError(error);
        if (task.getAttempts() >= MAX_ATTEMPTS) {
            task.setStatus(OutboxTask.Status.FAILED);
        } else {
            long backoffSeconds = (long) Math.pow(2, task.getAttempts()) * 5;
            task.setNextAttemptAt(Instant.now().plus(Duration.ofSeconds(backoffSeconds)));
        }
    }
}
