package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.annotation.FreeQuestionGroup;
import cn.duckflew.annotation.PaidQuestionGroup;
import cn.duckflew.entity.Question;
import cn.duckflew.entity.pay.Order;
import cn.duckflew.exception.QuestionIdInvalidException;
import cn.duckflew.service.AnswerService;
import cn.duckflew.service.ConsultAreaService;
import cn.duckflew.service.QuestionService;
import cn.duckflew.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * 咨询问题 相关接口
 */
@SuppressWarnings("AlibabaClassMustHaveAuthor")
@RequestMapping("/question")
@RestController
@Validated
public class QuestionController
{


    @Autowired
    QuestionService questionService;

    /**
     * 用户发布问题
     * @param param 问题参数
     * @return
     */
    @SaCheckLogin
    @PostMapping("/submit")
    public SaResult submitQuestion(@Validated({FreeQuestionGroup.class}) @RequestBody SubmitQuestionParam param)
    {
        Question dest = new Question();
        BeanUtils.copyProperties(param,dest);
        dest.setCreateTime(new Date());
        dest.setUpdateTime(new Date());
        questionService.pubQuestion(dest,param.getProfessorIds(),param.getConsultAreaIdList(),param.getQuestionDescImgList());
        return SaResult.ok().setMsg("发布问题成功,请等待管理员审核");
    }
    /**
     * 用户发布付费提问
     * @param param 问题参数
     * @return
     */
    @SaCheckLogin
    @PostMapping("/paid")
    public SaResult addPaidQuestion(@Validated({PaidQuestionGroup.class}) @RequestBody SubmitQuestionParam param)
    {
        Question dest = new Question();
        BeanUtils.copyProperties(param,dest);
        dest.setCreateTime(new Date());
        dest.setUpdateTime(new Date());
        Order order=questionService.pubPaidQuestion(dest,param.getProfessorIds().get(0),param.getConsultAreaIdList(),param.getQuestionDescImgList());
        return SaResult.ok().setMsg("发布问题成功,请等待管理员审核").setData(order);
    }


    @Autowired
    AnswerService answerService;
    /**
     * 根据问题id获取问题以及回答的列表
     * @param questionId 问题id
     * @return
     */
    @GetMapping("/{questionId}")
    public SaResult getQuestionById(@PathVariable
                                    @NotNull(message = "问题id不能为空")
                                    Integer questionId)
    {
        Map<String,Object> res=new HashMap<>();
        List<AnswerVoToUser> ansList = answerService.getAnswerInfoListByQuestionId(questionId, StpUtil.getLoginIdAsInt());
        QuestionVoToUser questionInfo = questionService.getQuestionInfoById(questionId);
        res.put("questionInfo",questionInfo);
        res.put("answerList",ansList);
        return SaResult.ok().setMsg("获取问题详情成功").setData(res);
    }

    /**
     * 根据用户id获取提问
     * @return
     */
    @GetMapping("/user/{userId}")
    @SaCheckLogin
    public SaResult getAllMyQuestion(
            @NotNull(message = "用户id不能为空")
            @PathVariable Integer userId)
    {
        List<QuestionVoToUser> questionInfoList= questionService.getAllMyQuestion(userId);
        return SaResult.ok().setData(questionInfoList);
    }

    @Autowired
    ConsultAreaService consultAreaService;
    /**
     * 获取问答推荐
     */
    @GetMapping("/recommend")
    @SaCheckLogin
    public SaResult recommend(@NotNull(message = "分页数不能为空") Integer pageNum,
                              @NotNull(message = "分页大小不能为空")Integer pageSize)
    {
        int userId = StpUtil.getLoginIdAsInt();
        PageRes<QuestionAndAnswer> res = questionService.recommendQAToUser(pageNum, pageSize, userId);
        return SaResult.ok().setData(res);
    }


    /**
     * 给问题点赞
     * @param likeQuestionParam
     * @return
     */
    @PostMapping("/like")
    @SaCheckLogin
    public SaResult likeQuestion(@RequestBody @Validated LikeQuestionParam likeQuestionParam)
    {
        int userId = StpUtil.getLoginIdAsInt();
        Question question = questionService.getById(likeQuestionParam.getQuestionId());
        if (question==null)
        {
            throw new QuestionIdInvalidException("给问题点赞出现id无效",likeQuestionParam.getQuestionId());
        }
        questionService.likeQuestion(userId,question,likeQuestionParam.getStatus());
        return SaResult.ok().setMsg("操作成功");
    }
}
