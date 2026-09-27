package cn.duckflew.education.qa;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import cn.duckflew.education.qa.dto.QuestionCard;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/questions")
public class PublicQuestionController {

    private final QuestionService questionService;

    public PublicQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ApiResponse<PageResponse<QuestionCard>> list(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok(PageResponse.of(questionService.search(null, pageable)));
    }
}
