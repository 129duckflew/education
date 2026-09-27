package cn.duckflew.education.qa;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import cn.duckflew.education.order.PayOrder;
import cn.duckflew.education.qa.dto.QuestionCard;
import cn.duckflew.education.qa.dto.QuestionDetail;
import cn.duckflew.education.qa.dto.SubmitPaidQuestionRequest;
import cn.duckflew.education.qa.dto.SubmitQuestionRequest;
import cn.duckflew.education.security.CurrentUser;
import cn.duckflew.education.user.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    public QuestionController(QuestionService questionService, UserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<QuestionDetail> submit(@Valid @RequestBody SubmitQuestionRequest request) {
        Question question = questionService.submitFree(CurrentUser.id(), request);
        return ApiResponse.ok(questionService.detail(question.getId(), CurrentUser.id(), CurrentUser.role()));
    }

    @PostMapping("/paid")
    public ApiResponse<PayOrder> submitPaid(@Valid @RequestBody SubmitPaidQuestionRequest request) {
        return ApiResponse.ok(questionService.submitPaid(CurrentUser.id(), request));
    }

    @GetMapping("/{id}")
    public ApiResponse<QuestionDetail> detail(@PathVariable Long id) {
        return ApiResponse.ok(questionService.detail(id, CurrentUser.idOrNull(), CurrentUser.roleOrNull()));
    }

    @GetMapping("/mine")
    public ApiResponse<PageResponse<QuestionCard>> mine(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(PageResponse.of(questionService.listMine(CurrentUser.id(),
                PageRequest.of(page, size))));
    }

    @GetMapping("/recommend")
    public ApiResponse<PageResponse<QuestionCard>> recommend(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Long userId = CurrentUser.id();
        List<Long> areas = userService.interestAreas(userId);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok(PageResponse.of(
                questionService.recommendByAreas(areas, userId, pageable)));
    }

    @PostMapping("/{id}/like")
    public ApiResponse<Void> like(@PathVariable Long id) {
        questionService.like(CurrentUser.id(), id);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}/like")
    public ApiResponse<Void> unlike(@PathVariable Long id) {
        questionService.unlike(CurrentUser.id(), id);
        return ApiResponse.ok();
    }
}
