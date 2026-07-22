package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.professor.Answer;
import cn.duckflew.service.AnswerService;
import cn.duckflew.service.SystemMsgService;
import cn.duckflew.service.VoService;
import cn.duckflew.vo.AnswerVoToAdmin;
import cn.duckflew.vo.PageRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台管理:回答相关接口
 */
@RestController
@RequestMapping("/answer")
public class AnswerController
{

    @Autowired
    SystemMsgService systemMsgService;
    /**
     * 对回答进行封禁
     * @param ansId
     * @return
     */
    @PostMapping("/ban/{ansId}")
    @SaCheckLogin
    public SaResult banQuestion(@PathVariable Integer ansId )
    {
        systemMsgService.generateBanAnswerMsg(ansId);
        return SaResult.ok().setMsg("封禁回答成功");
    }

    @Autowired
    AnswerService answerService;
    /**
     * 根据关键词搜索回答
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
        PageRes<AnswerVoToAdmin> res = answerService.searchAnswer(pageNum, pageSize, keyword);
        return SaResult.ok().setData(res);
    }

    @Autowired
    VoService voService;
    /**
     * 根据id获取回答
     * @param answerId
     * @return
     */
    @GetMapping("/{answerId}")
    public SaResult getAnswerById(
            @NotNull(message = "回答id不能为空")
            @PathVariable Integer answerId
    )
    {
        Answer ans = answerService.getById(answerId);
        AnswerVoToAdmin voRes = voService.answerVoToAdmin(ans);
        return SaResult.ok().setData(voRes);
    }

    /**
     * 分页获取回答
     * @param pageNum 分页数(0为第一页)
     * @param pageSize 分页大小
     * @return
     */
    @GetMapping("/")
    public SaResult getAllAuditQuestion(
            @RequestParam(required = false,defaultValue = "0")
                    Integer pageNum,
            @RequestParam(required = false,defaultValue = "5")
                    Integer pageSize)
    {
        Page<Answer> page = new Page<>(pageNum, pageSize);
        answerService.page(page);
        PageRes<AnswerVoToAdmin> pageRes = new PageRes<>();
        List<AnswerVoToAdmin> list = page.getRecords().stream().map(voService::answerVoToAdmin).collect(Collectors.toList());
        pageRes.setTotal(page.getTotal());
        pageRes.setList(list);
        return SaResult.ok().setData(pageRes);
    }
}
