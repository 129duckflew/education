package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.annotation.ProfessorAccess;
import cn.duckflew.entity.professor.Answer;
import cn.duckflew.enums.QuestionStatus;
import cn.duckflew.service.*;
import cn.duckflew.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 回答相关接口
 */
@RestController
@RequestMapping("/answer")
@Slf4j
public class AnswerController
{

    @Autowired
    BaseUserService baseUserService;


    @Autowired
    ProfessorService professorService;
    /**
     * 教授对问题发表回答或者修改已有的回答
     * @param answerParams
     * @return
     */
    @PostMapping("/")
    @SaCheckLogin
    @ProfessorAccess
    public SaResult pubAnswer(@Validated @RequestBody AnswerParams answerParams)
    {
        Answer answer = new Answer();
        BeanUtils.copyProperties(answerParams,answer);
        answer.setCreateTime(new Date());
        answer.setProfessorId(StpUtil.getLoginIdAsInt());
        answer.setAnswerStatus(QuestionStatus.NORMAL.getCode());
        answerService.pubAnswer(answer);
        return SaResult.ok().setMsg("发布回答成功");
    }

    /**
     * 给回答点赞/取消点赞
     * @param likeAnswerParam 参数
     * @return
     */
    @PostMapping("/like")
    public SaResult likeAnswer(@Validated @RequestBody LikeAnswerParam likeAnswerParam)
    {
        log.debug("回答点赞参数:{}",likeAnswerParam);
        return  baseUserService.likeAnswer(likeAnswerParam.getAnswerId(), StpUtil.getLoginIdAsInt(),likeAnswerParam.getStatus());
    }

    /**
     * 给回答添加收藏/取消收藏
     * @param param 参数
     * @return
     */
    @SaCheckLogin
    @PostMapping("/collect")
    public SaResult collectAnswer(@RequestBody CollectAnswerParam param )
    {
        return  baseUserService.collectAnswer(param.getAnswerId(),StpUtil.getLoginIdAsInt(), param.getCollectStatus());
    }


    @Autowired
    UserOpService userOpService;
    @Autowired
    AnswerService answerService;
    /**
     * 根据用户id获取收藏的回答
     * @apiNote 需要登录
     */
    @GetMapping("/collect/{userId}")
    public SaResult getCollectAnswer(
            @NotNull(message = "用户id不能为空")
            @PathVariable Integer userId
    )
    {
        List<AnswerVoToUser> collectAnswerInfo = answerService.getCollectAnswerInfo(userId);
        return SaResult.ok().setData(collectAnswerInfo);
    }

    @Autowired
    VoService voService;

    /**
     * 根据回答id获取详情
     * @param answerId
     * @return
     */
    @GetMapping("/{answerId}")
    public SaResult getAnswerInfoById(
            @NotNull(message = "问题id不能为空")
            @PathVariable Integer answerId)
    {
        AnswerVoToUser ansInfo = voService.answerVoToUser(answerService.getById(answerId));
        return SaResult.ok().setData(ansInfo);
    }


}
