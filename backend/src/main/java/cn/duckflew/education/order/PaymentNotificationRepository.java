package cn.duckflew.education.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentNotificationRepository extends JpaRepository<PaymentNotification, Long> {
    boolean existsByProviderAndProviderTradeNo(String provider, String providerTradeNo);
}
