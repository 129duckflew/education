package cn.duckflew.education.search;

import cn.duckflew.education.guide.GuideService;
import cn.duckflew.education.news.NewsService;
import cn.duckflew.education.professor.ProfessorService;
import cn.duckflew.education.qa.QuestionService;
import cn.duckflew.education.resource.ResourceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 全站搜索聚合：问答 / 教授 / 资讯 / 资料 / 指南。
 * 问答与教授基于 pg_trgm 相似度；其余为名称模糊匹配。
 */
@Service
public class SearchService {

    private final QuestionService questionService;
    private final ProfessorService professorService;
    private final NewsService newsService;
    private final ResourceService resourceService;
    private final GuideService guideService;

    public SearchService(QuestionService questionService,
                         ProfessorService professorService,
                         NewsService newsService,
                         ResourceService resourceService,
                         GuideService guideService) {
        this.questionService = questionService;
        this.professorService = professorService;
        this.newsService = newsService;
        this.resourceService = resourceService;
        this.guideService = guideService;
    }

    @Transactional(readOnly = true)
    public SearchResult search(String keyword, int limit) {
        if (keyword == null || keyword.isBlank()) {
            return new SearchResult(keyword, List.of(), List.of(), List.of(), List.of(), List.of());
        }
        String kw = keyword.trim();
        PageRequest pageable = PageRequest.of(0, limit);

        var questions = questionService.search(kw, pageable).getContent();
        var professors = professorService.search(kw, PageRequest.of(0, limit)).getContent();
        var news = newsService.search(kw, pageable).getContent();
        var resources = resourceService.search(kw,
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
        var guides = guideService.search(kw);

        return new SearchResult(kw, questions, professors, news, resources, guides);
    }
}
