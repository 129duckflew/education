package cn.duckflew.education.qa;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import cn.duckflew.education.qa.dto.AuditQuestionRequest;
import cn.duckflew.education.qa.dto.QuestionCard;
import cn.duckflew.education.qa.dto.QuestionDetail;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/questions")
@PreAuthorize("hasAuthority('question:manage')")
public class AdminQuestionController {

    private final QuestionService questionService;

    public AdminQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ApiResponse<PageResponse<QuestionCard>> list(
            @RequestParam(required = false) QuestionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = questionService.adminSearch(status, pageable);
        // 管理端可见全部状态，这里复用公开装配逻辑
        var cards = questionService.detailCards(result.getContent());
        return ApiResponse.ok(new PageResponse<>(cards, result.getTotalElements(), page, size));
    }

    @PostMapping("/audit")
    public ApiResponse<QuestionDetail> audit(@Valid @RequestBody AuditQuestionRequest request) {
        Question question = questionService.audit(request.questionId(), request.status());
        return ApiResponse.ok(questionService.detail(question.getId(), null, null));
    }
}
