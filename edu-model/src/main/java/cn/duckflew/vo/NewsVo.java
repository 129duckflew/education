package cn.duckflew.vo;

import cn.duckflew.entity.News;
import cn.duckflew.entity.NewsFrom;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;


@Data
@EqualsAndHashCode(callSuper = false)
public class NewsVo extends News
{
    /**
     * 管理员昵称
     */
    private String adminNickName;

    /**
     * 相关news
     */
    private List<News> relatedNews;
    /**
     * 来源列表
     */
    private List<NewsFrom> newsFromList;
    public NewsVo(News news)
    {
        super(
                news.getId(),
                news.getNewsTitle(),
                news.getCreateTime(),
                news.getCreateBy(),
                news.getNewsContent(),
                news.getPriority(),
                news.getCover(),
                news.getIndexShow()
        );
    }
}
