package cn.duckflew.education.admin;

import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRepository;
import cn.duckflew.education.user.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 首次启动时创建默认管理员账号（可用 app.bootstrap.admin.enabled=false 关闭）。
 */
@Slf4j
@Component
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final AppRoleRepository appRoleRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:true}")
    private boolean enabled;

    @Value("${app.bootstrap.admin.username:admin}")
    private String username;

    @Value("${app.bootstrap.admin.password:admin123}")
    private String password;

    public AdminBootstrap(UserRepository userRepository,
                          AppRoleRepository appRoleRepository,
                          AdminRoleRepository adminRoleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.appRoleRepository = appRoleRepository;
        this.adminRoleRepository = adminRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled || userRepository.existsByUsername(username)) {
            return;
        }
        User admin = new User();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(UserRole.ADMIN);
        admin.setNickname("超级管理员");
        admin.setEnabled(true);
        userRepository.save(admin);

        appRoleRepository.findByCode("SUPER_ADMIN").ifPresent(role -> {
            AdminRole link = new AdminRole();
            link.setUserId(admin.getId());
            link.setRoleId(role.getId());
            adminRoleRepository.save(link);
        });
        log.info("已创建默认管理员账号 username={} password={}（请尽快修改）", username, password);
    }
}
