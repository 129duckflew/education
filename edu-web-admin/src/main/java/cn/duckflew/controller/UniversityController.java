package cn.duckflew.controller;

import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.University;
import cn.duckflew.service.UniversityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 学校相关接口
 */
@RestController
@RequestMapping("/university")
public class UniversityController
{

    @Autowired
    UniversityService universityService;

    /**
     * 根据关键词搜索大学，关键词可以为空
     * @param keyword
     * @return
     */
    @GetMapping("/")
    public SaResult getUniversityByKeyword(String keyword)
    {
        List<University> res;
        if (keyword==null||keyword.isEmpty())
            res=universityService.list()    ;
        else res=universityService.list(
                new QueryWrapper<University>()
                .like("school_name",keyword)
        );
        return SaResult.ok().setData(res);
    }
}
