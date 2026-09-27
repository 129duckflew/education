package cn.duckflew.education.professor;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professors")
public class ProfessorPublicController {

    private final ProfessorService professorService;

    public ProfessorPublicController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ProfessorSummary>> list(
            @RequestParam(required = false) Long jobRankId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("userId"));
        return ApiResponse.ok(PageResponse.of(professorService.list(jobRankId, pageable)));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<ProfessorSummary>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.ok(PageResponse.of(
                professorService.search(keyword, PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProfessorDetail> detail(@PathVariable Long id) {
        return ApiResponse.ok(professorService.detail(id));
    }

    @GetMapping("/{id}/reviews")
    public ApiResponse<List<ProfessorDetail.ReviewView>> reviews(@PathVariable Long id) {
        return ApiResponse.ok(professorService.reviews(id));
    }
}
