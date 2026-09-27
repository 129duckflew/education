package cn.duckflew.education.news;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/news")
@PreAuthorize("hasAuthority('news:manage')")
public class NewsAdminController {

    private final NewsService newsService;

    public NewsAdminController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping
    public ApiResponse<NewsDtos.NewsView> create(@Valid @RequestBody NewsDtos.SaveRequest request) {
        return ApiResponse.ok(newsService.save(null, CurrentUser.id(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<NewsDtos.NewsView> update(@PathVariable Long id,
                                                 @Valid @RequestBody NewsDtos.SaveRequest request) {
        return ApiResponse.ok(newsService.save(id, CurrentUser.id(), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ApiResponse.ok();
    }
}
