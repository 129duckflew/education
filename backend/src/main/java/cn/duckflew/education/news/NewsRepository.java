package cn.duckflew.education.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findAllByOrderByPriorityDescCreatedAtDesc(Pageable pageable);

    List<News> findByIndexShowTrueOrderByPriorityDescCreatedAtDesc(Pageable pageable);

    List<News> findByIdNot(Long id, Pageable pageable);
}
