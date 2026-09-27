package cn.duckflew.education.taxonomy;

import cn.duckflew.education.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/areas")
public class ConsultAreaAdminController {

    private final ConsultAreaService service;

    public ConsultAreaAdminController(ConsultAreaService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('taxonomy:manage')")
    public ApiResponse<ConsultArea> create(@Valid @RequestBody ConsultAreaRequest request) {
        return ApiResponse.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('taxonomy:manage')")
    public ApiResponse<ConsultArea> update(@PathVariable Long id,
                                           @Valid @RequestBody ConsultAreaRequest request) {
        return ApiResponse.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('taxonomy:manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok();
    }
}
