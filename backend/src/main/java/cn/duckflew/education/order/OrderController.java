package cn.duckflew.education.order;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import cn.duckflew.education.security.CurrentUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public ApiResponse<PageResponse<PayOrder>> mine(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok(PageResponse.of(orderService.listByUser(CurrentUser.id(), pageable)));
    }

    @GetMapping("/{id}")
    public ApiResponse<PayOrder> detail(@PathVariable Long id) {
        return ApiResponse.ok(paymentService.getOrderForUser(CurrentUser.id(), id));
    }
}
