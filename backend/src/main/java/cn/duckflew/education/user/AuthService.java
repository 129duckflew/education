package cn.duckflew.education.user;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.common.web.ClientInfo;
import cn.duckflew.education.security.JwtService;
import cn.duckflew.education.user.dto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       VerificationService verificationService,
                       LoginLogRepository loginLogRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.verificationService = verificationService;
        this.loginLogRepository = loginLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (request.email() == null && request.phone() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "邮箱和手机号至少提供一个");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        if (request.username() != null && userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (request.email() != null) {
            if (userRepository.existsByEmail(request.email())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            verificationService.verify(request.email(), VerificationPurpose.REGISTER, request.code());
        }
        if (request.phone() != null) {
            if (userRepository.existsByPhone(request.phone())) {
                throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
            }
            verificationService.verify(request.phone(), VerificationPurpose.REGISTER, request.code());
        }

        userRepository.save(user);
        recordLogin(user, "REGISTER");
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = findByAccount(request.account());
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        recordLogin(user, "PASSWORD");
        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(RefreshRequest request) {
        var jwt = jwtService.decode(request.refreshToken());
        if (!JwtService.TYPE_REFRESH.equals(jwt.getClaimAsString("typ"))) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "refreshToken 无效");
        }
        Long userId = Long.valueOf(jwt.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        return issueTokens(user);
    }

    private User findByAccount(String account) {
        return userRepository.findByUsername(account)
                .or(() -> userRepository.findByEmail(account))
                .or(() -> userRepository.findByPhone(account))
                .orElse(null);
    }

    private TokenResponse issueTokens(User user) {
        String access = jwtService.issueAccessToken(user);
        String refresh = jwtService.issueRefreshToken(user);
        return TokenResponse.of(access, refresh, jwtService.accessTokenTtlSeconds(), UserProfile.from(user));
    }

    private void recordLogin(User user, String type) {
        LoginLog log = new LoginLog();
        log.setUserId(user.getId());
        log.setLoginType(type);
        log.setIp(ClientInfo.ip());
        log.setDevice(ClientInfo.device());
        loginLogRepository.save(log);
    }
}
