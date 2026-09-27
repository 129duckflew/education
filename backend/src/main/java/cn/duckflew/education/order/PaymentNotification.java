package cn.duckflew.education.order;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 支付回调幂等记录。
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_notification")
public class PaymentNotification extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_trade_no", nullable = false)
    private String providerTradeNo;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "raw_payload")
    private String rawPayload;
}
