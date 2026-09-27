package cn.duckflew.education.order;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.security.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}")
    public ApiResponse<String> create(@PathVariable Long orderId) {
        return ApiResponse.ok(paymentService.createPayment(CurrentUser.id(), orderId));
    }

    /**
     * Mock 渠道确认（仅开发用）。
     */
    @PostMapping("/mock/confirm")
    public ApiResponse<Void> mockConfirm(@RequestParam String orderNo) {
        paymentService.handlePaid(orderNo, null, "mock");
        return ApiResponse.ok();
    }

    /**
     * 真实支付渠道异步回调入口（示例）。
     */
    @PostMapping("/notify")
    public ApiResponse<Void> notify(@RequestBody Map<String, String> payload) {
        String orderNo = payload.get("out_trade_no");
        String tradeNo = payload.get("trade_no");
        paymentService.handlePaid(orderNo, tradeNo, payload.toString());
        return ApiResponse.ok();
    }
}
