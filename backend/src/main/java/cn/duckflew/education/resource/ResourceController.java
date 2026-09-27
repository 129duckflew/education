package cn.duckflew.education.resource;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import cn.duckflew.education.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ResourceDtos.ResourceView>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok(PageResponse.of(resourceService.search(keyword, pageable)));
    }

    @GetMapping("/mine")
    public ApiResponse<PageResponse<ResourceDtos.ResourceView>> mine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok(PageResponse.of(resourceService.listMine(CurrentUser.id(), pageable)));
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ApiResponse<Long> publish(@Valid @RequestBody ResourceDtos.CreateRequest request) {
        return ApiResponse.ok(resourceService.publish(CurrentUser.id(), request));
    }
}
