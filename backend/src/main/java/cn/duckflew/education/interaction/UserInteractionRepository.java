package cn.duckflew.education.interaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    boolean existsByUserAIdAndUserBId(Long userAId, Long userBId);
}
