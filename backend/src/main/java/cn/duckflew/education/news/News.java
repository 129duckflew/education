package cn.duckflew.education.news;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "news")
public class News extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String content;

    @Column(name = "cover_file_id")
    private Long coverFileId;

    @Column(nullable = false)
    private int priority;

    @Column(name = "index_show", nullable = false)
    private boolean indexShow;

    @Column(name = "author_id")
    private Long authorId;
}
