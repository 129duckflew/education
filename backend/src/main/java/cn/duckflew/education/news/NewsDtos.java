package cn.duckflew.education.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class NewsDtos {

    private NewsDtos() {
    }

    public record SourceInput(@NotBlank @Size(max = 255) String title,
                              @NotBlank @Size(max = 1000) String url) {
    }

    public record SaveRequest(
            @NotBlank(message = "标题不能为空") @Size(max = 255) String title,
            String content,
            Long coverFileId,
            Integer priority,
            Boolean indexShow,
            List<SourceInput> sources
    ) {
    }

    public record SourceView(Long id, String title, String url) {
    }

    public record NewsView(
            Long id,
            String title,
            String content,
            Long coverFileId,
            int priority,
            boolean indexShow,
            java.time.Instant createdAt,
            List<SourceView> sources
    ) {
    }
}
