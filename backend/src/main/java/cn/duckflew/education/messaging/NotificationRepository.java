package cn.duckflew.education.messaging;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByToUserIdOrderByCreatedAtDesc(Long toUserId, Pageable pageable);

    Page<Notification> findByToUserIdAndTypeOrderByCreatedAtDesc(Long toUserId, NotificationType type, Pageable pageable);

    long countByToUserIdAndReadFalse(Long toUserId);

    long countByToUserIdAndTypeAndReadFalse(Long toUserId, NotificationType type);
}
