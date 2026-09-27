package cn.duckflew.education.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    Page<LoginLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
