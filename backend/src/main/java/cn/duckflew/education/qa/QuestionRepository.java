package cn.duckflew.education.qa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);

    long countByStatus(QuestionStatus status);

    long countByCreatedAtBetween(Instant from, Instant to);

    List<Question> findByIdIn(Collection<Long> ids);

    @Query("""
            select distinct q from Question q, QuestionArea qa
            where qa.questionId = q.id and qa.areaId in :areaIds and q.status = :status
            order by q.createdAt desc
            """)
    Page<Question> findRecommended(@Param("areaIds") Collection<Long> areaIds,
                                   @Param("status") QuestionStatus status,
                                   Pageable pageable);

    /**
     * 基于 pg_trgm 的模糊检索，按标题相似度排序。
     */
    @Query(value = """
            select * from question q
            where q.status = 'NORMAL'
              and (q.title % :kw or coalesce(q.description, '') % :kw)
            order by similarity(q.title, :kw) desc, q.created_at desc
            """,
            countQuery = """
            select count(*) from question q
            where q.status = 'NORMAL'
              and (q.title % :kw or coalesce(q.description, '') % :kw)
            """,
            nativeQuery = true)
    Page<Question> searchNormal(@Param("kw") String keyword, Pageable pageable);
}
