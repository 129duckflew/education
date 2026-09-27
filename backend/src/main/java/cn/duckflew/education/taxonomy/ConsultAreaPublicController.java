package cn.duckflew.education.taxonomy;

import cn.duckflew.education.common.api.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/areas")
public class ConsultAreaPublicController {

    private final ConsultAreaService service;

    public ConsultAreaPublicController(ConsultAreaService service) {
        this.service = service;
    }

    @GetMapping("/tree")
    public ApiResponse<List<ConsultAreaNode>> tree() {
        return ApiResponse.ok(service.tree());
    }

    @GetMapping("/flat")
    public ApiResponse<List<ConsultArea>> flat() {
        return ApiResponse.ok(service.findAll());
    }
}
