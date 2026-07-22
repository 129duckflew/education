package cn.duckflew.service;

import cn.duckflew.entity.News;
import cn.duckflew.entity.NewsFrom;
import cn.duckflew.entity.admin.Admin;
import cn.duckflew.exception.NewsIdInvalidException;
import cn.duckflew.mapper.NewsFromMapper;
import cn.duckflew.mapper.NewsMapper;
import cn.duckflew.mapper.admin.AdminMapper;
import cn.duckflew.vo.NewsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Slf4j
public class NewsService extends ServiceImpl<NewsMapper, News>
{
    @Autowired
    NewsMapper newsMapper;
    @Autowired
    AdminMapper adminMapper;
    @Autowired
    NewsFromMapper newsFromMapper;
    @Transactional(rollbackFor = Exception.class)
    public void updateNews(News news,List<NewsFrom>newsFromList)
    {
        if (newsMapper.selectById(news.getId())==null)
            throw new NewsIdInvalidException();
        newsMapper.updateById(news);
        if (newsFromList!=null&&!newsFromList.isEmpty())
        {
            newsFromMapper.delete(
                    new QueryWrapper<NewsFrom>()
                    .eq("news_id",news.getId())
            );
            newsFromList.forEach(newsFrom -> {
                newsFrom.setNewsId(news.getId());
                newsFromMapper.insert(newsFrom);
            });
        }
    }


    public List<News> relatedNews(Integer size,News sourceNews)
    {
        List<News> relatedNews = newsMapper.selectList(new QueryWrapper<News>().ge("create_time", sourceNews.getCreateTime()).last("limit " + size));
        return relatedNews;
    }
    public NewsVo newsToVo(News news)
    {
        NewsVo newsVo = new NewsVo(news);
        newsVo.setRelatedNews(relatedNews(3,news));
        Admin admin = adminMapper.selectById(news.getCreateBy());
        String name = admin.getRealName();
        newsVo.setAdminNickName(name);
        List<NewsFrom> newsFromList = newsFromMapper.selectList(new QueryWrapper<NewsFrom>().eq("news_id", news.getId()));
        newsVo.setNewsFromList(newsFromList);
        return newsVo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteNewsById(Integer newsId)
    {
        newsMapper.deleteById(newsId);
        newsFromMapper.delete(
                new QueryWrapper<NewsFrom>()
                .eq("news_id",newsId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void addNews(News news, List<NewsFrom> newsFromList)
    {
        newsMapper.insert(news);
        newsFromList.forEach(newsFrom -> {
            newsFrom.setNewsId(news.getId());
            newsFromMapper.insert(newsFrom);
        });
        log.info("user:{}发布了news,newsId={}",news.getCreateBy(),news.getId());
    }

    public String uploadNewsCoverFile(Integer userId, MultipartFile file)
    {
        return null;
    }
}
