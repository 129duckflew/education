package cn.duckflew.education.guide;

import cn.duckflew.education.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/study-guides")
@PreAuthorize("hasAuthority('guide:manage')")
public class GuideAdminController {

    private final GuideService guideService;

    public GuideAdminController(GuideService guideService) {
        this.guideService = guideService;
    }

    @PostMapping
    public ApiResponse<StudyGuide> create(@Valid @RequestBody GuideDtos.SaveRequest request) {
        return ApiResponse.ok(guideService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<StudyGuide> update(@PathVariable Long id,
                                          @Valid @RequestBody GuideDtos.SaveRequest request) {
        return ApiResponse.ok(guideService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        guideService.delete(id);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/areas")
    public ApiResponse<Void> setAreas(@PathVariable Long id,
                                      @Valid @RequestBody GuideDtos.AreasRequest request) {
        guideService.setAreas(id, request.areaIds());
        return ApiResponse.ok();
    }

    @GetMapping("/{id}/areas")
    public ApiResponse<List<Long>> areas(@PathVariable Long id) {
        return ApiResponse.ok(guideService.areasOf(id));
    }
}
