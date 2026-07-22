package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.News;
import cn.duckflew.service.NewsService;
import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import cn.duckflew.vo.NewsVo;
import cn.duckflew.vo.admin.NewsParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理:资讯相关接口
 */
@RestController
@RequestMapping("/news")
public class NewsController
{
    @Autowired
    NewsService newsService;

    /**
     * 分页获取所有资讯
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/")
    @SaCheckLogin
    @SaCheckPermission("news")
    public SaResult getAllPreProfessor(
            @RequestParam(defaultValue = "0",required = false)
                    Integer pageNum,
            @RequestParam(defaultValue = "5",required = false)
                    Integer pageSize
    )
    {
        Page<News> page = new Page<>(pageNum, pageSize);
        newsService.page(page);
        List<NewsVo> newsVoList = page.getRecords().stream().map(news -> newsService.newsToVo(news)).collect(Collectors.toList());
        Map<String,Object> res=new HashMap<>();
        res.put("newsList",newsVoList);
        res.put("total",page.getTotal());
        return SaResult.ok().setData(res);
    }
    /**
     * 更新资讯信息
     * @param newsParam 更新资讯的参数
     * @return
     * @apiNote 需要更新什么字段就填什么，不更新的为null，id必填，并且要求有效，后端会校验
     */
    @PutMapping
    @SaCheckLogin
    @SaCheckPermission("news")
    public SaResult updateNews(@RequestBody @Validated({UpdateGroup.class}) NewsParam newsParam)
    {
        News news = new News();
        BeanUtils.copyProperties(newsParam,news);
        news.setId(newsParam.getNewsId());
        newsService.updateNews(news,newsParam.getNewsFromList());
        return SaResult.ok().setMsg("更新成功");
    }

    /**
     * 添加资讯
     * @param addNewsParam 参数
     * @return
     */
    @PostMapping
    @SaCheckLogin
    @SaCheckPermission("news")
    public SaResult addNews(@RequestBody @Validated({AddGroup.class}) NewsParam addNewsParam)
    {
        News news = new News();
        BeanUtils.copyProperties(addNewsParam,news);
        news.setCreateTime(new Date());
        Integer  userId = StpUtil.getLoginIdAsInt();
        news.setCreateBy(userId);
        newsService.addNews(news,addNewsParam.getNewsFromList());
        return SaResult.ok().setMsg("添加资讯成功");
    }

    /**
     * 根据id删除资讯
     * @param newsId
     * @return
     */
    @DeleteMapping("/{newsId}")
    @SaCheckLogin
    @SaCheckPermission("news")
    public SaResult deleteNews(@PathVariable Integer newsId)
    {
        newsService.deleteNewsById(newsId);
        return SaResult.ok().setMsg("删除成功");
    }
}
