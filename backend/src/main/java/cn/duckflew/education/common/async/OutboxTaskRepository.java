package cn.duckflew.education.common.async;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface OutboxTaskRepository extends JpaRepository<OutboxTask, Long> {

    List<OutboxTask> findByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
            OutboxTask.Status status, Instant now, Pageable pageable);
}
