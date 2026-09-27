package cn.duckflew.education.user;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    private String nickname;

    @Column(name = "real_name")
    private String realName;

    @Column(name = "avatar_file_id")
    private Long avatarFileId;

    private String gender;

    private LocalDate birthday;

    @Column(nullable = false)
    private boolean enabled = true;
}
