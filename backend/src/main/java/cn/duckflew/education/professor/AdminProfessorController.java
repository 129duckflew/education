package cn.duckflew.education.professor;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/professors")
@PreAuthorize("hasAuthority('professor:manage')")
public class AdminProfessorController {

    private final ProfessorService professorService;

    public AdminProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ProfessorSummary>> list(
            @RequestParam(defaultValue = "false") boolean approved,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("userId"));
        var result = approved
                ? professorService.list(null, pageable)
                : professorService.pending(pageable);
        return ApiResponse.ok(PageResponse.of(result));
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        professorService.approve(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id) {
        professorService.reject(id);
        return ApiResponse.ok();
    }
}
