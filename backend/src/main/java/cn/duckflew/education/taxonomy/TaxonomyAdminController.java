package cn.duckflew.education.taxonomy;

import cn.duckflew.education.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/taxonomy")
@PreAuthorize("hasAuthority('taxonomy:manage')")
public class TaxonomyAdminController {

    private final TaxonomyService service;

    public TaxonomyAdminController(TaxonomyService service) {
        this.service = service;
    }

    @PostMapping("/universities")
    public ApiResponse<University> createUniversity(@Valid @RequestBody TaxonomyRequests.UniversityRequest request) {
        return ApiResponse.ok(service.saveUniversity(null, request));
    }

    @PutMapping("/universities/{id}")
    public ApiResponse<University> updateUniversity(@PathVariable Long id,
                                                    @Valid @RequestBody TaxonomyRequests.UniversityRequest request) {
        return ApiResponse.ok(service.saveUniversity(id, request));
    }

    @DeleteMapping("/universities/{id}")
    public ApiResponse<Void> deleteUniversity(@PathVariable Long id) {
        service.deleteUniversity(id);
        return ApiResponse.ok();
    }

    @PostMapping("/majors")
    public ApiResponse<UniversityMajor> createMajor(@Valid @RequestBody TaxonomyRequests.MajorRequest request) {
        return ApiResponse.ok(service.saveMajor(null, request));
    }

    @PutMapping("/majors/{id}")
    public ApiResponse<UniversityMajor> updateMajor(@PathVariable Long id,
                                                    @Valid @RequestBody TaxonomyRequests.MajorRequest request) {
        return ApiResponse.ok(service.saveMajor(id, request));
    }

    @DeleteMapping("/majors/{id}")
    public ApiResponse<Void> deleteMajor(@PathVariable Long id) {
        service.deleteMajor(id);
        return ApiResponse.ok();
    }

    @PostMapping("/directions")
    public ApiResponse<ResearchDirection> createDirection(@Valid @RequestBody TaxonomyRequests.ResearchDirectionRequest request) {
        return ApiResponse.ok(service.saveDirection(null, request));
    }

    @PutMapping("/directions/{id}")
    public ApiResponse<ResearchDirection> updateDirection(@PathVariable Long id,
                                                          @Valid @RequestBody TaxonomyRequests.ResearchDirectionRequest request) {
        return ApiResponse.ok(service.saveDirection(id, request));
    }

    @DeleteMapping("/directions/{id}")
    public ApiResponse<Void> deleteDirection(@PathVariable Long id) {
        service.deleteDirection(id);
        return ApiResponse.ok();
    }
}
