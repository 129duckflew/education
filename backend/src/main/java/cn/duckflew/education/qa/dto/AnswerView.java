package cn.duckflew.education.qa.dto;

import java.time.Instant;

public record AnswerView(
        Long id,
        Long professorId,
        String professorName,
        String content,
        long likeCount,
        long collectCount,
        boolean liked,
        boolean collected,
        Instant createdAt
) {
}
