package cn.duckflew.education.user;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.common.api.PageResponse;
import cn.duckflew.education.user.dto.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:manage')")
    public ApiResponse<PageResponse<UserProfile>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserProfile> result = userService.search(keyword, role,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ApiResponse.ok(PageResponse.of(result));
    }

    @PatchMapping("/{id}/enabled")
    @PreAuthorize("hasAuthority('user:manage')")
    public ApiResponse<UserProfile> setEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        return ApiResponse.ok(userService.setEnabled(id, enabled));
    }
}
