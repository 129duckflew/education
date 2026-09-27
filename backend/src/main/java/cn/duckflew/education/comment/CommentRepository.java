package cn.duckflew.education.comment;

import cn.duckflew.education.qa.IdCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTargetTypeAndTargetIdOrderByCreatedAtAsc(CommentTargetType targetType, Long targetId);

    long countByTargetTypeAndTargetId(CommentTargetType targetType, Long targetId);

    @Query("""
            select new cn.duckflew.education.qa.IdCount(c.targetId, count(c))
            from Comment c where c.targetType = :type and c.targetId in :ids group by c.targetId
            """)
    List<IdCount> countByTargetIds(@Param("type") CommentTargetType type, @Param("ids") Collection<Long> ids);
}
