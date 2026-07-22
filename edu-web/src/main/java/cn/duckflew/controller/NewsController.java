package cn.duckflew.controller;

import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.News;
import cn.duckflew.service.NewsService;
import cn.duckflew.vo.NewsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资讯相关接口
 */
@RestController
@RequestMapping("/news")
public class NewsController
{


    @Autowired
    NewsService newsService;
    /**
     * 获取首页资讯
     * @return
     * @apiNote 不需要登录
     */
    @GetMapping("/index")
    public SaResult getIndexNews()
    {
        List<News> newsList=newsService.list(
                new QueryWrapper<News>()
                .eq("index_show",1)
                .orderByAsc("priority")
        );
        List<NewsVo> res = newsList.stream().map(news -> newsService.newsToVo(news)).collect(Collectors.toList());
        return SaResult.ok().setData(res);
    }

    /**
     * 根据id获取资讯
     * @param newsId 资讯id
     * @return
     */
    @GetMapping("/{newsId}")
    public SaResult getNewsById(@PathVariable Integer newsId)
    {
        News news = newsService.getById(newsId);
        NewsVo vo = newsService.newsToVo(news);
        return SaResult.ok().setData(vo);
    }
}
