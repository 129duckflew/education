package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.annotation.ProfessorAccess;
import cn.duckflew.document.ProfessorInfoDoc;
import cn.duckflew.entity.ConsultFeedback;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.service.*;
import cn.duckflew.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教授相关接口
 */
@SuppressWarnings("AlibabaClassMustHaveAuthor")
@RequestMapping("/professor")
@RestController
@Validated
@Slf4j
public class ProfessorController
{


    @Autowired
    BaseUserService baseUserService;


    /**
     * 获取所有的教授
     * @return
     * @response {
     *     "code": 200,
     *     "msg": "ok",
     *     "data": [
     *         {
     *             "id": 5,
     *             "telephoneNumber": null,
     *             "username": "duckflew",
     *             "password": null,
     *             "email": "129duckflew@gmail.com",
     *             "birthday": "2001-01-10",
     *             "gender": "男",
     *             "nickName": "李亮",
     *             "avatar": null,
     *             "cardId": "4212***********0014",
     *             "userType": 3,
     *             "realName": "李亮",
     *             "jobRankId": null,
     *             "cvFileName": "70de302e-11be-47c9-9e79-a78e34c1bb3d.docx"
     *         }
     *     ]
     * }
     */

    @Autowired
    ProfessorAnswerAreaService professorAnswerAreaService;

    /**
     * 教授指定想要回答的问题的领域
     * @param setAnswerArea 问题属于的领域的Id数组
     * @return
     * @apiNote 问题领域可以指定多个,如果设置对父级领域感兴趣，则默认为对此父级领域下的所有子领域都感兴趣，
     * 例如 1:大学-->{2:工学  3:  农学} 如果指定为1感兴趣 则就包括了工学和农学 推荐的时候遇到工学和农学
     * 会直接进行推荐, 所以如果只是部分感兴趣，只需要提交最底层的叶子节点的领域id就行
     * @response {
     *     "code": 200,
     *     "msg": "设置回答问题的版块成功",
     *     "data": null
     * }
     */
    @SaCheckLogin
    @PostMapping("/area/answer")
    @ProfessorAccess
    public SaResult setAnswerArea(@RequestBody @Validated ConsultAreaIds setAnswerArea)
    {
        int userId = StpUtil.getLoginIdAsInt();
        log.info("{}教授修改回答领域,新领域List:{}",userId,setAnswerArea.getAreaIds());
        professorService.setAnswerArea(userId,setAnswerArea.getAreaIds());
        return SaResult.ok().setMsg("设置回答问题的版块成功");
    }

    /**
     * 根据咨询领域获取对应的教授列表
     * @param pageNum
     * @param pageSize
     * @param areaIds 领域id数组
     * @return
     * @apiNote 传参方式:areaIds=1&areaIds=2&pageNum=111
     */
    @GetMapping("/list/by_area")
    public SaResult getProfessorListByAreaIds(
            @RequestParam(value = "pageNum",defaultValue = "0",required = false) Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "5",required = false) Integer pageSize,
            @NotEmpty(message = "领域id数组不能为空")
            @RequestParam("areaIds") String []areaIds)
    {
        if (areaIds==null)return SaResult.ok();
        List<String> areaIdList = new ArrayList<>(Arrays.asList(areaIds));
        return SaResult.ok().setData(professorService.getProfessorListByAreaIds(pageNum,pageSize,areaIdList));
    }
    /**
     * 根据关键词搜索教授信息
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/by_keyword")
    public SaResult searchProfessor(
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "keyword",required = false) String keyword
    )
    {
        PageRes<ProfessorInfo> pageRes = professorService.searchProfessorByKeyword(
                pageNum,pageSize,keyword
        );
        return SaResult.ok().setData(pageRes);
    }



    @Autowired
    ProInfoService proInfoService;

    /**
     * 修改个人简介
     * @param introduction 个人介绍
     * @return
     * @apiNote 注意传参方式是 form-data 不是 json
     */
    @PostMapping("/introduction")
    @SaCheckLogin
    @ProfessorAccess
    public SaResult setIntroduction(
            @NotBlank(message = "教授简介不能为空")
            @RequestParam("introduction") String introduction
    )
    {
        int professorId = StpUtil.getLoginIdAsInt();
        proInfoService.setIntroduction(professorId,introduction );
        return SaResult.ok().setMsg("设置简介成功");
    }
    /**
     * 修改付费问答价格
     * @param price 单次问答价格
     * @return
     * @apiNote 注意传参方式是 form-data 不是 json
     */
    @PostMapping("/question_price")
    @SaCheckLogin
    @ProfessorAccess
    public SaResult setPrice(
            @NotNull(message = "问答价格不能为空")
            @RequestParam("price") BigDecimal price
    )
    {
        int professorId = StpUtil.getLoginIdAsInt();
        proInfoService.setPrice(professorId,price );
        return SaResult.ok().setMsg("设置价格成功");
    }
    /**
     * 根据教授id获取教授简介
     */
    @GetMapping("/introduction/{proId}")
    public SaResult getIntroduction(@PathVariable Integer proId)
    {
        BaseUser professor = baseUserService.getById(proId);
        ProfessorInfo res = professorService.professorInfo(professor);
        return SaResult.ok().setData(res);
    }

    @Autowired
    ProfessorService professorService;

    @Autowired
    ConsultFeedbackService consultFeedbackService;
    /**
     * 对教授发布评价
     * @param evaluationParam 评价参数
     * @return
     */
    @PostMapping("/evaluation")
    @SaCheckLogin
    public SaResult addEvaluation(@RequestBody @Validated ProfessorEvaluationParam evaluationParam)
    {
        Integer userId = StpUtil.getLoginIdAsInt();
        consultFeedbackService.addConsultFeedback(
                userId,
                evaluationParam.getProfessorId(),
                evaluationParam.getEvaluation(),
                evaluationParam.getStar()
        );
        return SaResult.ok();
    }

    /**
     * 查询教授评价
     * @param pageSize 分页大小
     * @param pageNum 分页数
     * @param professorId 教授id
     * @return
     * @apiNote 分页参数可以为空，默认0 5
     */
    @GetMapping("/evaluation/{professorId}")
    @SaCheckLogin
    @ProfessorAccess
    public SaResult getEvaluation(
            @RequestParam(required = false,defaultValue = "5")
            Integer pageSize,
            @RequestParam(required = false,defaultValue = "0")
            Integer pageNum,
            @PathVariable
            @NotNull(message = "教授id不能为空")
            Integer professorId
    )
    {
        Page<ConsultFeedback> page = new Page<>(pageNum,pageSize);
        QueryWrapper<ConsultFeedback> queryWrapper = new QueryWrapper<ConsultFeedback>().eq("professor_id", professorId);
        List<ConsultFeedbackVo> res = consultFeedbackService.page(page,queryWrapper)
                .getRecords()
                .stream()
                .map(cf->consultFeedbackService.loadVo(cf))
                .collect(Collectors.toList());
        return SaResult.ok().setData(res);
    }

}
