package cn.duckflew.education.taxonomy;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/taxonomy")
public class TaxonomyPublicController {

    private final TaxonomyService service;

    public TaxonomyPublicController(TaxonomyService service) {
        this.service = service;
    }

    @GetMapping("/universities")
    public ApiResponse<PageResponse<University>> universities(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(PageResponse.of(
                service.searchUniversities(name, PageRequest.of(page, size, Sort.by("id")))));
    }

    @GetMapping("/majors")
    public ApiResponse<java.util.List<UniversityMajor>> majors(@RequestParam(required = false) Long parentId) {
        return ApiResponse.ok(service.listMajors(parentId));
    }

    @GetMapping("/directions")
    public ApiResponse<java.util.List<ResearchDirection>> directions(@RequestParam(required = false) Long majorId) {
        return ApiResponse.ok(service.listDirections(majorId));
    }
}
