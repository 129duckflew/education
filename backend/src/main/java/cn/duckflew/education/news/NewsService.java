package cn.duckflew.education.news;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsSourceRepository sourceRepository;

    public NewsService(NewsRepository newsRepository, NewsSourceRepository sourceRepository) {
        this.newsRepository = newsRepository;
        this.sourceRepository = sourceRepository;
    }

    @Transactional(readOnly = true)
    public Page<NewsDtos.NewsView> list(Pageable pageable) {
        Page<News> page = newsRepository.findAllByOrderByPriorityDescCreatedAtDesc(pageable);
        return new PageImpl<>(toViews(page.getContent()), page.getPageable(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<NewsDtos.NewsView> index() {
        return toViews(newsRepository.findByIndexShowTrueOrderByPriorityDescCreatedAtDesc(PageRequest.of(0, 8)));
    }

    @Transactional(readOnly = true)
    public NewsDtos.NewsView detail(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "资讯不存在"));
        return toViews(List.of(news)).get(0);
    }

    @Transactional(readOnly = true)
    public List<NewsDtos.NewsView> related(Long id, int limit) {
        return toViews(newsRepository.findByIdNot(id, PageRequest.of(0, limit)));
    }

    @Transactional(readOnly = true)
    public Page<NewsDtos.NewsView> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }
        Page<News> page = newsRepository
                .findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword.trim(), pageable);
        return new PageImpl<>(toViews(page.getContent()), page.getPageable(), page.getTotalElements());
    }

    @Transactional
    public NewsDtos.NewsView save(Long id, Long authorId, NewsDtos.SaveRequest request) {
        News news = id == null ? new News() : newsRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "资讯不存在"));
        news.setTitle(request.title());
        news.setContent(request.content());
        news.setCoverFileId(request.coverFileId());
        news.setPriority(request.priority() == null ? 0 : request.priority());
        news.setIndexShow(request.indexShow() != null && request.indexShow());
        news.setAuthorId(authorId);
        newsRepository.save(news);

        sourceRepository.deleteByNewsId(news.getId());
        if (request.sources() != null) {
            List<NewsSource> sources = request.sources().stream().map(s -> {
                NewsSource source = new NewsSource();
                source.setNewsId(news.getId());
                source.setTitle(s.title());
                source.setUrl(s.url());
                return source;
            }).toList();
            sourceRepository.saveAll(sources);
        }
        return toViews(List.of(news)).get(0);
    }

    @Transactional
    public void delete(Long id) {
        newsRepository.deleteById(id);
    }

    private List<NewsDtos.NewsView> toViews(List<News> newsList) {
        if (newsList.isEmpty()) {
            return List.of();
        }
        List<Long> ids = newsList.stream().map(News::getId).toList();
        Map<Long, List<NewsDtos.SourceView>> sourcesByNews = sourceRepository.findByNewsIdIn(ids).stream()
                .collect(Collectors.groupingBy(NewsSource::getNewsId,
                        Collectors.mapping(s -> new NewsDtos.SourceView(s.getId(), s.getTitle(), s.getUrl()),
                                Collectors.toList())));
        return newsList.stream().map(n -> new NewsDtos.NewsView(
                n.getId(), n.getTitle(), n.getContent(), n.getCoverFileId(),
                n.getPriority(), n.isIndexShow(), n.getCreatedAt(),
                sourcesByNews.getOrDefault(n.getId(), List.of()))).toList();
    }
}
