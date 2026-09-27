package cn.duckflew.education.professor;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping("/apply")
    public ApiResponse<Long> apply(@Valid @RequestBody ProfessorRequests.ApplyRequest request) {
        return ApiResponse.ok(professorService.apply(CurrentUser.id(), request).getUserId());
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ApiResponse<ProfessorSummary> updateMe(@Valid @RequestBody ProfessorRequests.UpdateRequest request) {
        var profile = professorService.updateMe(CurrentUser.id(), request);
        return ApiResponse.ok(professorService.detail(profile.getUserId()).summary());
    }

    @PostMapping("/me/areas")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ApiResponse<Void> setAreas(@Valid @RequestBody ProfessorRequests.AreasRequest request) {
        professorService.setAreas(CurrentUser.id(), request.areaIds());
        return ApiResponse.ok();
    }

    @PutMapping("/me/educations")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ApiResponse<Void> setEducations(@Valid @RequestBody List<ProfessorRequests.EducationRequest> requests) {
        professorService.replaceEducations(CurrentUser.id(), requests);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/reviews")
    public ApiResponse<Void> review(@PathVariable Long id,
                                    @Valid @RequestBody ProfessorRequests.ReviewRequest request) {
        professorService.addReview(CurrentUser.id(), id, request);
        return ApiResponse.ok();
    }
}
