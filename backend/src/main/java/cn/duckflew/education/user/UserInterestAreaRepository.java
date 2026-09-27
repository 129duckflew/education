package cn.duckflew.education.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestAreaRepository extends JpaRepository<UserInterestArea, Long> {
    List<UserInterestArea> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
