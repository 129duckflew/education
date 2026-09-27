package cn.duckflew.education.qa;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.qa.dto.AnswerRequest;
import cn.duckflew.education.qa.dto.AnswerView;
import cn.duckflew.education.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ApiResponse<Long> publish(@Valid @RequestBody AnswerRequest request) {
        return ApiResponse.ok(answerService.publish(CurrentUser.id(), request).getId());
    }

    @GetMapping("/question/{questionId}")
    public ApiResponse<List<AnswerView>> listByQuestion(@PathVariable Long questionId) {
        return ApiResponse.ok(answerService.listByQuestion(questionId, CurrentUser.idOrNull()));
    }

    @GetMapping("/collected")
    public ApiResponse<List<AnswerView>> collected() {
        return ApiResponse.ok(answerService.listCollected(CurrentUser.id()));
    }

    @PostMapping("/{id}/like")
    public ApiResponse<Void> like(@PathVariable Long id) {
        answerService.like(CurrentUser.id(), id);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}/like")
    public ApiResponse<Void> unlike(@PathVariable Long id) {
        answerService.unlike(CurrentUser.id(), id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/collect")
    public ApiResponse<Void> collect(@PathVariable Long id) {
        answerService.collect(CurrentUser.id(), id);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}/collect")
    public ApiResponse<Void> uncollect(@PathVariable Long id) {
        answerService.uncollect(CurrentUser.id(), id);
        return ApiResponse.ok();
    }
}
