package cn.duckflew.controller;

import cn.dev33.satoken.util.SaResult;
import cn.duckflew.document.QuestionAndAnswerDoc;
import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.Question;
import cn.duckflew.entity.QuestionArea;
import cn.duckflew.entity.professor.Answer;
import cn.duckflew.service.*;
import cn.duckflew.vo.PageRes;
import cn.duckflew.vo.QuestionAndAnswer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索相关接口
 */
@RequestMapping("/search")
@RestController
public class SearchController
{

    @Autowired
    EsService esService;

    @Autowired
    AnswerService answerService;
    @Autowired
    ConsultAreaService consultAreaService;
    @Autowired
    QuestionService questionService;


    @Autowired
    VoService voService;

    @Autowired
    QuestionAreaService questionAreaService;
    /**
     * 关键词分页搜索问答
     * @param pageNum 分页数
     * @param pageSize 分页大小
     * @param keyword 关键词
     * @return
     * @apiNote 支持中文分词
     */
    @GetMapping("/qa")
    public SaResult search(
            @RequestParam(value = "pageNum",required = false,defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize",required = false,defaultValue = "5") Integer pageSize,
            @RequestParam("keyword") String keyword
    )
    {
        Page<QuestionAndAnswerDoc> source = esService.simpleSearchPage(pageNum, pageSize, keyword);
        PageRes<QuestionAndAnswer> res = new PageRes<>();
        List<QuestionAndAnswer> collect = source.stream().map(qaDoc ->
        {
            Question question = questionService.getById(qaDoc.getQuestionId());
            Answer answer = answerService.getById(qaDoc.getAnswerId());
            QuestionAndAnswer qa = new QuestionAndAnswer();
            BeanUtils.copyProperties(question,qa);
            qa.setAnswer(voService.answerVoToUser(answer));
            List<String> areaIdList = questionAreaService.list(new QueryWrapper<QuestionArea>().eq("question_id", qaDoc.getQuestionId())).stream().map(QuestionArea::getConsultAreaId).collect(Collectors.toList());
            if (!areaIdList.isEmpty())
                qa.setConsultAreaList(consultAreaService.listByIds(areaIdList));
            return qa;
        }).collect(Collectors.toList());
        res.setList(collect);
        res.setTotal(source.getTotalElements());
        return SaResult.ok().setData(res);
    }






}
