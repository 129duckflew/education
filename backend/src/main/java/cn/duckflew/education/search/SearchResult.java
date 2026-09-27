package cn.duckflew.education.search;

import cn.duckflew.education.guide.GuideDtos;
import cn.duckflew.education.news.NewsDtos;
import cn.duckflew.education.professor.ProfessorSummary;
import cn.duckflew.education.qa.dto.QuestionCard;
import cn.duckflew.education.resource.ResourceDtos;

import java.util.List;

/**
 * 全站搜索结果：按类型分组。
 */
public record SearchResult(
        String keyword,
        List<QuestionCard> questions,
        List<ProfessorSummary> professors,
        List<NewsDtos.NewsView> news,
        List<ResourceDtos.ResourceView> resources,
        List<GuideDtos.GuideNode> guides
) {
    public long total() {
        return (long) questions.size() + professors.size() + news.size()
                + resources.size() + guides.size();
    }
}
