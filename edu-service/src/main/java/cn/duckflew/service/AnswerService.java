package cn.duckflew.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.duckflew.document.QuestionAndAnswerDoc;
import cn.duckflew.entity.Question;
import cn.duckflew.entity.professor.Answer;
import cn.duckflew.entity.system.SystemMsg;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.entity.user.UserOp;
import cn.duckflew.enums.SysMsgType;
import cn.duckflew.enums.UserOpType;
import cn.duckflew.exception.QuestionIdInvalidException;
import cn.duckflew.mapper.*;
import cn.duckflew.repo.QuestionAndAnswerRepository;
import cn.duckflew.vo.AnswerVoToAdmin;
import cn.duckflew.vo.AnswerVoToUser;
import cn.duckflew.vo.PageRes;
import cn.duckflew.vo.QuestionAndAnswer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnswerService extends ServiceImpl<AnswerMapper, Answer>
{

    @Autowired
    VoService voService;
    @Autowired
    UserOpMapper userOpMapper;
    @Autowired
    BaseUserMapper baseUserMapper;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    AnswerMapper answerMapper;





    /**
     * answerList装饰上问题详情信息 如点赞数 回答人等等
     * @param answerList
     * @param userId
     * @return
     */
    public List<AnswerVoToUser> answerListToInfoList(List<Answer> answerList, Integer userId)
    {
        List<AnswerVoToUser> answers =answerList
                .stream().map(voService::answerVoToUser).collect(Collectors.toList());
        return answers;
    }

    /**
     * 获取问题下的所有回答
     * @param questionId
     * @return
     */
    public List<AnswerVoToUser> getAnswerInfoListByQuestionId(Integer questionId, Integer userId)
    {
        List<Answer>answerList=answerMapper.selectList(
                new QueryWrapper<Answer>()
                .eq("question_id",questionId)
        );
        return  answerListToInfoList(answerList,userId);
    }


    /**
     * 获取收藏的所有回答
     * @param userId 用户id
     * @return
     */
    public List<AnswerVoToUser> getCollectAnswerInfo(Integer userId)
    {
        List<Integer> answerIds=
                userOpMapper.selectList(
                        new QueryWrapper<UserOp>()
                                .eq("user_id",userId)
                                .eq("op_type", UserOpType.COLLECT_ANSWER.getCode())
                                .eq("op_status",1)
                ).stream().map(UserOp::getResourceId).collect(Collectors.toList());
        if (answerIds.isEmpty()) return null;
        List<Answer> answerList = answerMapper.selectBatchIds(answerIds);
        return answerList.stream().map(voService::answerVoToUser).collect(Collectors.toList());
    }

    public Answer getQuesTopAnswer(int questionId)
    {
       Integer topAnsId=answerMapper.getQuesTopAnswerId(questionId);
       if (topAnsId==null)return null;
       return getById(topAnsId);
    }

    @Autowired
    SystemMsgMapper systemMsgMapper;
    @Transactional(rollbackFor = Exception.class)
    public void  pubAnswer(Answer answer)
    {
        Answer exist = answerMapper.selectOne(new QueryWrapper<Answer>().eq("professor_id", answer.getProfessorId()).eq("question_id", answer.getQuestionId()));
        if (exist!=null)
        {
            exist.setUpdateTime(new Date());
            saveOrUpdate(exist);
            log.warn("修改了回答:{}",exist);
            log.info("更新 es doc：{}",exist);
            addAnswerDoc(exist);
            return;
        }
        SystemMsg systemMsg = SystemMsg.baseMsg();
        systemMsg.setMsgType(SysMsgType.GET_ANSWER.getCode());
        systemMsg.setFromUserId(0);
        Question question = questionMapper.selectById(answer.getQuestionId());
        if (question==null) throw new QuestionIdInvalidException("发布回答时发现问题id无效",answer.getQuestionId());
        systemMsg.setToUserId(question.getUserId());
        systemMsg.setRelatedUserId(answer.getProfessorId());
        answerMapper.insert(answer);
        log.info("添加 es doc：{}",answer);
        addAnswerDoc(answer);
        log.info("插入回答记录,生成系统消息还未插入，事务未结束 answer={}",answer);
        systemMsg.setResourceId(answer.getId());
        systemMsgMapper.insert(systemMsg);
        log.info("教授发布回答，生成系统消息通知:{}",systemMsg);
    }


    @Autowired
    QuestionAndAnswerRepository questionAndAnswerRepository;
    public void addAnswerDoc(@NotNull(message = "生成doc的answer不能为空") Answer answer)
    {
        QuestionAndAnswerDoc qa = voService.answerToQADoc(answer);
        questionAndAnswerRepository.save(qa);
    }
    @Autowired
    BaseUserService baseUserService;

    public PageRes<AnswerVoToAdmin> searchAnswer(Integer pageNum, Integer pageSize, String keyword)
    {
        Pageable page= PageRequest.of(pageNum, pageSize);
        Page<QuestionAndAnswerDoc> esPage = questionAndAnswerRepository.findQuestionAndAnswerByAnswerText(keyword, page);
        List<Integer> answerIds = esPage.getContent().stream().map(QuestionAndAnswerDoc::getAnswerId).collect(Collectors.toList());
        PageRes<AnswerVoToAdmin> res = new PageRes<>();
        if (answerIds.isEmpty())
            res.setTotal(0L);
        else {
            List<Answer> answerList = answerMapper.selectBatchIds(answerIds);
            res.setList(
                    answerList.stream().map(voService::answerVoToAdmin).collect(Collectors.toList())
            );
            res.setTotal(esPage.getTotalElements());
        }
        return res;
    }

    public Integer getAnsNum(Date startTime, Date endTime)
    {
        return answerMapper.selectCount(
                new QueryWrapper<Answer>()
                        .ge("create_time",startTime)
                        .le("create_time",endTime)
        );
    }
}
