package cn.duckflew.education.messaging;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByToUserIdOrderByCreatedAtDesc(Long toUserId, Pageable pageable);

    Page<Notification> findByToUserIdAndTypeOrderByCreatedAtDesc(Long toUserId, NotificationType type, Pageable pageable);

    long countByToUserIdAndReadFalse(Long toUserId);

    long countByToUserIdAndTypeAndReadFalse(Long toUserId, NotificationType type);

    java.util.Optional<Notification> findByIdAndToUserId(Long id, Long toUserId);

    @Modifying
    @Query("update Notification n set n.read = true where n.toUserId = :userId and n.read = false")
    int markAllRead(@Param("userId") Long userId);
}
