package cn.duckflew.education.order;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {

    private final PayOrderRepository repository;

    public OrderService(PayOrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PayOrder create(Long userId, BigDecimal amount, String subject) {
        PayOrder order = new PayOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAmount(amount);
        order.setSubject(subject);
        order.setStatus(OrderStatus.TO_PAY);
        return repository.save(order);
    }

    @Transactional(readOnly = true)
    public PayOrder getRequired(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<PayOrder> listByUser(Long userId, Pageable pageable) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public PayOrder markPaid(PayOrder order, String provider, String providerTradeNo) {
        if (order.getStatus() == OrderStatus.PAID) {
            return order;
        }
        order.setStatus(OrderStatus.PAID);
        order.setProvider(provider);
        order.setProviderTradeNo(providerTradeNo);
        order.setPaidAt(Instant.now());
        return repository.save(order);
    }

    private String generateOrderNo() {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(
                java.time.LocalDateTime.now());
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "E" + timestamp + random;
    }
}
