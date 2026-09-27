package cn.duckflew.education.professor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProfessorProfileRepository extends JpaRepository<ProfessorProfile, Long> {

    Page<ProfessorProfile> findByApproved(boolean approved, Pageable pageable);

    List<ProfessorProfile> findByApprovedTrue();

    Page<ProfessorProfile> findByApprovedTrueAndJobRankId(Long jobRankId, Pageable pageable);

    /**
     * 基于 pg_trgm 的教授模糊检索（姓名 / 简介）。
     */
    @Query(value = """
            select p.* from professor_profile p
            join app_user u on u.id = p.user_id
            where p.approved = true
              and (coalesce(u.real_name, '') % :kw or coalesce(p.introduction, '') % :kw)
            order by similarity(coalesce(u.real_name, ''), :kw) desc
            """,
            countQuery = """
            select count(*) from professor_profile p
            join app_user u on u.id = p.user_id
            where p.approved = true
              and (coalesce(u.real_name, '') % :kw or coalesce(p.introduction, '') % :kw)
            """,
            nativeQuery = true)
    Page<ProfessorProfile> search(@Param("kw") String keyword, Pageable pageable);
}
