package cn.duckflew.education.news;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsPublicController {

    private final NewsService newsService;

    public NewsPublicController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ApiResponse<PageResponse<NewsDtos.NewsView>> list(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(PageResponse.of(newsService.list(PageRequest.of(page, size))));
    }

    @GetMapping("/index")
    public ApiResponse<List<NewsDtos.NewsView>> index() {
        return ApiResponse.ok(newsService.index());
    }

    @GetMapping("/{id}")
    public ApiResponse<NewsDtos.NewsView> detail(@PathVariable Long id) {
        return ApiResponse.ok(newsService.detail(id));
    }

    @GetMapping("/{id}/related")
    public ApiResponse<List<NewsDtos.NewsView>> related(@PathVariable Long id,
                                                        @RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.ok(newsService.related(id, limit));
    }
}
