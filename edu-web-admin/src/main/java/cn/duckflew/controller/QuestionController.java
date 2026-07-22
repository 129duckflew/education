package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.Question;
import cn.duckflew.enums.QuestionStatus;
import cn.duckflew.service.QuestionService;
import cn.duckflew.service.SystemMsgService;
import cn.duckflew.vo.AuditQuestionParam;
import cn.duckflew.vo.PageRes;
import cn.duckflew.vo.QuestionVoToAdmin;
import cn.duckflew.vo.QuestionVoToUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理:问题相关接口
 */
@RestController
@RequestMapping("/question")
@Validated
public class QuestionController
{

    @Autowired
    QuestionService questionService;

    /**
     * 获取不同状态的问题
     * @param pageNum 分页数(0为第一页)
     * @param pageSize 分页大小
     * @param questionStatusCode 问题状态码
     * @return
     * @apiNote questionStatusCode含义: 0:被封禁 1:正常 2:待审核/审核中 3:审核不通过
     *
     */
    @GetMapping("/")
    public SaResult getAllAuditQuestion(
            @RequestParam(required = false,defaultValue = "0")
            Integer pageNum,
            @RequestParam(required = false,defaultValue = "5")
            Integer pageSize,
            @RequestParam
            Integer questionStatusCode
    )
    {
        Page<Question> page = new Page<>(pageNum, pageSize);
        questionService.page(
                page,
                new QueryWrapper<Question>().
                    eq("question_status",
                    questionStatusCode
        ));
        List<QuestionVoToAdmin> questInfoList =
                page.getRecords().stream().map(questionService::questionVoToAdmin).collect(Collectors.toList());
        PageRes<QuestionVoToAdmin> res = new PageRes<>();
        res.setList(questInfoList);
        res.setTotal(page.getTotal());
        return SaResult.ok().setData(res);
    }

    @Autowired
    SystemMsgService systemMsgService;

    /**
     * 管理员对问题进行审核/禁用
     * @param auditQuestionParam 问题审核参数
     * @return
     */
    @SaCheckLogin
    @PostMapping("/handle")
    public SaResult auditQuestion(@Validated  @RequestBody AuditQuestionParam auditQuestionParam)
    {
        return questionService.auditQuestion(auditQuestionParam);
    }

    /**
     * 根据关键词搜索问题
     * @param keyword 关键词
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/search")
    public SaResult getQuestionByKeyword(
            @NotBlank(message = "关键词不能为空") String keyword,
            @RequestParam(required = false,defaultValue = "0") Integer pageNum,
            @RequestParam(required = false,defaultValue = "5") Integer pageSize
    )
    {
        Page<Question> page = questionService.searchQuestion(pageNum, pageSize, keyword);
        PageRes<QuestionVoToAdmin> res = new PageRes<>();
        res.setList(page.getRecords().stream().map(questionService::questionVoToAdmin).collect(Collectors.toList()));
        res.setTotal(page.getTotal());
        return SaResult.ok().setData(res);
    }
}
