package cn.duckflew.education.user;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.security.CurrentUser;
import cn.duckflew.education.user.dto.ChangePasswordRequest;
import cn.duckflew.education.user.dto.InterestAreaRequest;
import cn.duckflew.education.user.dto.UpdateProfileRequest;
import cn.duckflew.education.user.dto.UserProfile;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfile> me() {
        return ApiResponse.ok(userService.profile(CurrentUser.id()));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfile> updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok(userService.updateProfile(CurrentUser.id(), request));
    }

    @PostMapping("/me/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(CurrentUser.id(), request);
        return ApiResponse.ok();
    }

    @GetMapping("/me/interest-areas")
    public ApiResponse<List<Long>> interestAreas() {
        return ApiResponse.ok(userService.interestAreas(CurrentUser.id()));
    }

    @PutMapping("/me/interest-areas")
    public ApiResponse<Void> setInterestAreas(@Valid @RequestBody InterestAreaRequest request) {
        userService.setInterestAreas(CurrentUser.id(), request.areaIds());
        return ApiResponse.ok();
    }
}
