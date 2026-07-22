package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.StudyGuide;
import cn.duckflew.service.StudyGuideService;
import cn.duckflew.vo.RecommendStudyGuidePage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 学习导图相关接口
 */
@RequestMapping("/studyGuide")
@RestController
public class StudyGuideController
{


    @Autowired
    StudyGuideService studyGuideService;

    /**
     * 获取推荐的学习导图
     * @param pageNum 分页数
     * @param pageSize 分页大小
     * @return
     * @apiNote 这两个分页参数都不是必须的,拿到推荐的导图结果之后然后跳转具体id的页面展示详细学习导图
     * @response {
     *     "code": 200,
     *     "msg": "ok",
     *     "data": {
     *         "total": 1,
     *         "studyGuides": [
     *             {
     *                 "id": "1",
     *                 "nodeName": "Java后端开发工程师",
     *                 "isImportant": 0,
     *                 "parentId": "0"
     *             }
     *         ]
     *     }
     * }
     */
    @GetMapping("/recommend")
    @SaCheckLogin
    public SaResult recommendStudyGuideByArea(
            @NotNull(message = "分页数不能为空")
            @RequestParam(required = false,defaultValue = "0") Integer pageNum,
            @NotNull(message = "分页大小不能为空")
            @RequestParam(required = false,defaultValue = "5") Integer pageSize
            )
    {
        Integer userId = StpUtil.getLoginIdAsInt();
        RecommendStudyGuidePage studyGuidePage=studyGuideService.recommendStudyGuideByArea(userId,pageNum,pageSize);
        return SaResult.ok().setData(studyGuidePage);
    }

    /**
     * 根据id获取学习导图详情
     * @param guideId 导图id
     * @return
     */
    @GetMapping("/{guideId}")
    public SaResult getGuideById(
            @NotNull(message = "guideId不能为空")
            @PathVariable Integer guideId
    )
    {
        StudyGuide studyGuide = studyGuideService.getById(guideId);
        return SaResult.ok().setData(studyGuideService.guideToVo(studyGuide));
    }
}
