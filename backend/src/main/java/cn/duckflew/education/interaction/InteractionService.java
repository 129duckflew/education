package cn.duckflew.education.interaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 互动关系服务：私信准入的唯一判定来源。
 */
@Service
public class InteractionService {

    private final UserInteractionRepository repository;

    public InteractionService(UserInteractionRepository repository) {
        this.repository = repository;
    }

    /**
     * 记录两用户之间的互动。幂等；自己与自己不记录。
     */
    @Transactional
    public void record(Long userOne, Long userTwo) {
        if (userOne == null || userTwo == null || userOne.equals(userTwo)) {
            return;
        }
        Long a = Math.min(userOne, userTwo);
        Long b = Math.max(userOne, userTwo);
        if (!repository.existsByUserAIdAndUserBId(a, b)) {
            UserInteraction interaction = new UserInteraction();
            interaction.setUserAId(a);
            interaction.setUserBId(b);
            repository.save(interaction);
        }
    }

    /**
     * 是否允许建立私信会话（存在互动关系）。
     */
    @Transactional(readOnly = true)
    public boolean canMessage(Long userOne, Long userTwo) {
        if (userOne == null || userTwo == null || userOne.equals(userTwo)) {
            return false;
        }
        Long a = Math.min(userOne, userTwo);
        Long b = Math.max(userOne, userTwo);
        return repository.existsByUserAIdAndUserBId(a, b);
    }
}
