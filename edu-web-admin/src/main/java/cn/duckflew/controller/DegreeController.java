package cn.duckflew.controller;

import cn.dev33.satoken.util.SaResult;
import cn.duckflew.service.DegreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学位相关接口
 */
@RestController
@RequestMapping("/degree")
public class DegreeController
{

    @Autowired
    DegreeService degreeService;

    /**
     * 获取所有学位
     * @return
     */
    @GetMapping("/")
    public SaResult getAllDegree()
    {
       return SaResult.ok().setData(degreeService.list());
    }
}
