package cn.duckflew.education.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRoleRepository extends JpaRepository<AdminRole, Long> {
    List<AdminRole> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
