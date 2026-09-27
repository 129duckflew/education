package cn.duckflew.education.news;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NewsSourceRepository extends JpaRepository<NewsSource, Long> {
    List<NewsSource> findByNewsId(Long newsId);

    List<NewsSource> findByNewsIdIn(Collection<Long> newsIds);

    void deleteByNewsId(Long newsId);
}
