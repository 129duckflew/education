package cn.duckflew.education.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayOrderRepository extends JpaRepository<PayOrder, Long> {

    Optional<PayOrder> findByOrderNo(String orderNo);

    Page<PayOrder> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByOrderNo(String orderNo);
}
