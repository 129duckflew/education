package cn.duckflew.education.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public class CommentDtos {

    private CommentDtos() {
    }

    public record CreateRequest(
            @NotNull(message = "评论目标类型不能为空") CommentTargetType targetType,
            @NotNull(message = "评论目标不能为空") Long targetId,
            @NotBlank(message = "评论内容不能为空") @Size(max = 1000) String content,
            Long parentId
    ) {
    }

    public record CommentView(
            Long id,
            CommentTargetType targetType,
            Long targetId,
            Long userId,
            String userName,
            String userAvatar,
            String content,
            Long parentId,
            Instant createdAt,
            List<CommentView> replies
    ) {
    }
}
