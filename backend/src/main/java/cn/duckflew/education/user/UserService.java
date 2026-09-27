package cn.duckflew.education.user;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.taxonomy.ConsultAreaRepository;
import cn.duckflew.education.user.dto.ChangePasswordRequest;
import cn.duckflew.education.user.dto.UpdateProfileRequest;
import cn.duckflew.education.user.dto.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserInterestAreaRepository interestAreaRepository;
    private final ConsultAreaRepository consultAreaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserInterestAreaRepository interestAreaRepository,
                       ConsultAreaRepository consultAreaRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.interestAreaRepository = interestAreaRepository;
        this.consultAreaRepository = consultAreaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User getRequired(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserProfile profile(Long id) {
        return UserProfile.from(getRequired(id));
    }

    @Transactional
    public UserProfile updateProfile(Long id, UpdateProfileRequest request) {
        User user = getRequired(id);
        if (request.nickname() != null) {
            user.setNickname(request.nickname());
        }
        if (request.realName() != null) {
            user.setRealName(request.realName());
        }
        if (request.gender() != null) {
            user.setGender(request.gender());
        }
        if (request.birthday() != null) {
            user.setBirthday(request.birthday());
        }
        return UserProfile.from(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = getRequired(id);
        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "原密码不正确");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<Long> interestAreas(Long userId) {
        return interestAreaRepository.findByUserId(userId).stream()
                .map(UserInterestArea::getAreaId).toList();
    }

    @Transactional
    public void setInterestAreas(Long userId, List<Long> areaIds) {
        areaIds.forEach(areaId -> {
            if (!consultAreaRepository.existsById(areaId)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "领域不存在: " + areaId);
            }
        });
        interestAreaRepository.deleteByUserId(userId);
        List<UserInterestArea> entities = areaIds.stream().distinct().map(areaId -> {
            UserInterestArea entity = new UserInterestArea();
            entity.setUserId(userId);
            entity.setAreaId(areaId);
            return entity;
        }).toList();
        interestAreaRepository.saveAll(entities);
    }

    @Transactional(readOnly = true)
    public Page<UserProfile> search(String keyword, UserRole role, Pageable pageable) {
        String normalized = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return userRepository.search(normalized, role, pageable).map(UserProfile::from);
    }

    @Transactional
    public UserProfile setEnabled(Long id, boolean enabled) {
        User user = getRequired(id);
        user.setEnabled(enabled);
        return UserProfile.from(userRepository.save(user));
    }
}
