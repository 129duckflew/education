package cn.duckflew.education.order;

import org.springframework.stereotype.Component;

/**
 * 开发/演示用支付渠道：直接返回本地确认地址，不接入真实支付。
 */
@Component
public class MockPaymentProvider implements PaymentProvider {

    @Override
    public String name() {
        return "MOCK";
    }

    @Override
    public String payUrl(PayOrder order) {
        return "/api/payments/mock/confirm?orderNo=" + order.getOrderNo();
    }
}
