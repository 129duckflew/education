package cn.duckflew.education.search;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import cn.duckflew.education.professor.ProfessorService;
import cn.duckflew.education.professor.ProfessorSummary;
import cn.duckflew.education.qa.QuestionService;
import cn.duckflew.education.qa.dto.QuestionCard;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统一搜索入口，基于 PostgreSQL pg_trgm，替代原 Elasticsearch。
 */
@RestController
@RequestMapping("/api/public/search")
public class SearchController {

    private final QuestionService questionService;
    private final ProfessorService professorService;

    public SearchController(QuestionService questionService, ProfessorService professorService) {
        this.questionService = questionService;
        this.professorService = professorService;
    }

    @GetMapping("/questions")
    public ApiResponse<PageResponse<QuestionCard>> questions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size);
        return ApiResponse.ok(PageResponse.of(questionService.search(keyword, pageable)));
    }

    @GetMapping("/professors")
    public ApiResponse<PageResponse<ProfessorSummary>> professors(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("userId"));
        return ApiResponse.ok(PageResponse.of(professorService.search(keyword, pageable)));
    }
}
