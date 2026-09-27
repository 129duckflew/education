package cn.duckflew.education.order;

/**
 * 支付渠道抽象。默认提供 Mock 实现，可替换为支付宝/微信等。
 */
public interface PaymentProvider {

    String name();

    /**
     * 返回收银台跳转地址。
     */
    String payUrl(PayOrder order);
}
