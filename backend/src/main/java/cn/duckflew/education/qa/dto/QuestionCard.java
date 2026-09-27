package cn.duckflew.education.qa.dto;

import cn.duckflew.education.qa.QuestionStatus;

import java.time.Instant;
import java.util.List;

public record QuestionCard(
        Long id,
        String title,
        String description,
        QuestionStatus status,
        Long authorId,
        String authorName,
        List<Long> areaIds,
        List<String> areaNames,
        List<Long> imageFileIds,
        long likeCount,
        long answerCount,
        long commentCount,
        boolean liked,
        Instant createdAt
) {
}
