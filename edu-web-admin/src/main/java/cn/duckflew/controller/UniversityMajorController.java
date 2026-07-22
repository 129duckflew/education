package cn.duckflew.controller;

import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.UniversityMajor;
import cn.duckflew.service.UniversityMajorService;
import cn.duckflew.vo.UniversityMajorVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 大学专业相关接口
 */
@RestController
@RequestMapping("/major")
public class UniversityMajorController
{

    @Autowired
    UniversityMajorService universityMajorService;


    /**
     * 获取所有专业(树形结构)
     * @param keyword 搜索关键词 可以为空
     * @return
     */
    @GetMapping("/")
    public SaResult getAllMajor(String keyword)
    {
        List<UniversityMajorVo> res;
        if (keyword==null||keyword.isEmpty())
         res = universityMajorService.treeOfAll();
        else
        {
            List<UniversityMajor> list = universityMajorService.list(new QueryWrapper<UniversityMajor>().like("name", keyword));
            res = universityMajorService.universityMajorVo(list);
        }
        return SaResult.ok().setData(res);
    }
}
