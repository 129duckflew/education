package cn.duckflew.education.comment;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.security.CurrentUser;
import cn.duckflew.education.user.UserRole;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ApiResponse<List<CommentDtos.CommentView>> list(
            @RequestParam CommentTargetType targetType,
            @RequestParam Long targetId) {
        return ApiResponse.ok(commentService.list(targetType, targetId));
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody CommentDtos.CreateRequest request) {
        return ApiResponse.ok(commentService.create(CurrentUser.id(), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        commentService.delete(CurrentUser.id(), CurrentUser.role() == UserRole.ADMIN, id);
        return ApiResponse.ok();
    }
}
