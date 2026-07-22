package cn.duckflew.controller;

import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.professor.JobRank;
import cn.duckflew.service.JobRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 职称相关接口
 */
@RestController
@RequestMapping("/jobRank")
public class JobRankController
{

    @Autowired
    JobRankService jobRankService;
    /**
     * 获取所有职称
     * @return
     */
    @GetMapping("/")
    public SaResult getAllJobRank()
    {
        List<JobRank> res = jobRankService.list();
        return SaResult.ok().setData(res);
    }
}
