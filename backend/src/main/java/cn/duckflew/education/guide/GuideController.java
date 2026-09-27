package cn.duckflew.education.guide;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.security.CurrentUser;
import cn.duckflew.education.user.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-guides")
public class GuideController {

    private final GuideService guideService;
    private final UserService userService;

    public GuideController(GuideService guideService, UserService userService) {
        this.guideService = guideService;
        this.userService = userService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<GuideDtos.GuideNode>> tree() {
        return ApiResponse.ok(guideService.tree());
    }

    @GetMapping("/{id}")
    public ApiResponse<GuideDtos.GuideNode> node(@PathVariable Long id) {
        return ApiResponse.ok(guideService.findNode(id).orElse(null));
    }

    @GetMapping("/recommend")
    public ApiResponse<List<GuideDtos.GuideNode>> recommend() {
        Long userId = CurrentUser.id();
        return ApiResponse.ok(guideService.recommendByAreas(userService.interestAreas(userId)));
    }
}
