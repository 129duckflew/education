package cn.duckflew.education.order;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private final PayOrderRepository orderRepository;
    private final PaymentNotificationRepository notificationRepository;
    private final OrderService orderService;
    private final PaymentProvider provider;

    public PaymentService(PayOrderRepository orderRepository,
                          PaymentNotificationRepository notificationRepository,
                          OrderService orderService,
                          PaymentProvider provider) {
        this.orderRepository = orderRepository;
        this.notificationRepository = notificationRepository;
        this.orderService = orderService;
        this.provider = provider;
    }

    @Transactional(readOnly = true)
    public String createPayment(Long userId, Long orderId) {
        PayOrder order = orderService.getRequired(orderId);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权支付该订单");
        }
        if (order.getStatus() != OrderStatus.TO_PAY) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "订单状态不允许支付");
        }
        return provider.payUrl(order);
    }

    /**
     * 处理支付成功。按 (provider, tradeNo) 幂等，重复回调不重复置为已支付。
     */
    @Transactional
    public void handlePaid(String orderNo, String providerTradeNo, String rawPayload) {
        PayOrder order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        String tradeNo = providerTradeNo == null || providerTradeNo.isBlank()
                ? UUID.randomUUID().toString().replace("-", "")
                : providerTradeNo;

        if (notificationRepository.existsByProviderAndProviderTradeNo(provider.name(), tradeNo)) {
            return;
        }
        PaymentNotification notification = new PaymentNotification();
        notification.setProvider(provider.name());
        notification.setProviderTradeNo(tradeNo);
        notification.setOrderNo(orderNo);
        notification.setRawPayload(rawPayload);
        notificationRepository.save(notification);

        orderService.markPaid(order, provider.name(), tradeNo);
    }

    @Transactional(readOnly = true)
    public PayOrder getOrderForUser(Long userId, Long orderId) {
        PayOrder order = orderService.getRequired(orderId);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看该订单");
        }
        return order;
    }

    public Instant now() {
        return Instant.now();
    }
}
