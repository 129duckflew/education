package cn.duckflew.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.document.QuestionAndAnswerDoc;
import cn.duckflew.entity.*;
import cn.duckflew.entity.pay.Order;
import cn.duckflew.entity.professor.Answer;
import cn.duckflew.entity.system.SystemMsg;
import cn.duckflew.enums.OrderStatus;
import cn.duckflew.enums.QuestionStatus;
import cn.duckflew.enums.SysMsgType;
import cn.duckflew.enums.UserOpType;
import cn.duckflew.exception.NotProfessorException;
import cn.duckflew.exception.QuestionIdInvalidException;
import cn.duckflew.exception.QuestionStatusException;
import cn.duckflew.mapper.*;
import cn.duckflew.mapper.admin.ProInfoMapper;
import cn.duckflew.mapper.pay.OrderMapper;
import cn.duckflew.repo.QuestionAndAnswerRepository;
import cn.duckflew.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Service
@Slf4j
public class QuestionService extends ServiceImpl<QuestionMapper, Question>
{
    @Autowired
    SystemMsgMapper systemMsgMapper;
    @Autowired
    QuestionProfessorMapper questionProfessorMapper;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    BaseUserMapper baseUserMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SystemMsgService systemMsgService;


    public QuestionVoToAdmin questionVoToAdmin(Question question)
    {
        QuestionVoToAdmin res = new QuestionVoToAdmin();
        BeanUtils.copyProperties(question,res);
        res.setLikeNum(voService.getLikeNumOfQuestion(question.getId()));
        res.setAnsNum(voService.getAnsNumOfQuestionById(question.getId()));
        return res;
    }
    @Autowired
    QuestionDescImgMapper descImgMapper;
    @Transactional(rollbackFor = Exception.class)
    public void pubQuestion(Question question, List<Integer> proIds,List<String> areaIds,List<String> descImgList)
    {
        int curUserId = StpUtil.getLoginIdAsInt();
        question.setUserId(curUserId);
        question.setQuestionStatus(QuestionStatus.AUDITING.getCode());
        questionMapper.insert(question);
        for (Integer proId : proIds)
        {
           systemMsgService.addGetQuestionMsg(proId,question);
            /**
             * 生成提问记录
             */
            QuestionProfessor qp = new QuestionProfessor();
            qp.setQuestionId(question.getId());
            qp.setProfessorId(proId);
            questionProfessorMapper.insert(qp);
        }
        Set<String> areaIdSet = voService.areaIdSet(areaIds);
        areaIdSet.forEach(id->{
            QuestionArea qa = new QuestionArea();
            qa.setConsultAreaId(id);
            qa.setQuestionId(question.getId());
            questionAreaMapper.insert(qa);
        });
        if (descImgList!=null&&!descImgList.isEmpty())
        {
            descImgList.forEach(fileId->{
                QuestionDescImg questionDescImg = new QuestionDescImg();
                questionDescImg.setFileId(fileId);
                questionDescImg.setQuestionId(question.getId());
            });
        }
    }
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ProInfoMapper proInfoMapper;
    @Transactional(rollbackFor = Exception.class)
    public Order pubPaidQuestion(Question question, Integer proId,List<String> areaIds,List<String> descImgList)
    {
        pubQuestion(question, Collections.singletonList(proId),areaIds,descImgList);
        log.info("生成付费问答数据成功question:{}",question);
        ProInfo proInfo = proInfoMapper.selectById(proId);
        if (proInfo==null)throw new NotProfessorException("提问的目标用户不是教授,无法发起提问",proId);
        BigDecimal consultPrice = proInfo.getConsultPrice();
        Order order = new Order();
        Date date = new Date();
        order.setCreateTime(date);
        order.setOrderName("付费咨询订单");
        order.setId(new SimpleDateFormat("yyyyMMdd").format(date) + System.currentTimeMillis());
        order.setTotal(consultPrice);
        order.setOrderStatus(OrderStatus.TO_PAY.getCode());
        order.setUserId(question.getUserId());
        orderMapper.insert(order);
        log.info("用户:{}提交付费提问订单成功,订单号:{}",question.getUserId(),order.getId());
        question.setOrderId(order.getId());
        questionMapper.updateById(question);
        return order;
    }
    @Autowired
    QuestionAreaMapper questionAreaMapper;
    @Autowired
    VoService voService;

    @Transactional(rollbackFor = Exception.class)
    public SaResult auditQuestion(AuditQuestionParam auditQuestionParam)
    {
        Question question = questionMapper.selectById(auditQuestionParam.getQuestionId());
        if (question==null)throw new QuestionIdInvalidException("审核问题时问题id不存在",auditQuestionParam.getQuestionId());
        question.setQuestionStatus(auditQuestionParam.getQuestionStatus());
        SystemMsg systemMsg = SystemMsg.baseMsg();
        systemMsg.setResourceId(question.getId());
        if (auditQuestionParam.getQuestionStatus().equals(QuestionStatus.FORBIDDEN.getCode()))
        {
            systemMsg.setMsgType(SysMsgType.BAN_QUESTION.getCode());
        }
        else  if (auditQuestionParam.getQuestionStatus().equals(QuestionStatus.NORMAL.getCode()))
        {
            systemMsg.setMsgType(SysMsgType.QUESTION_AUDIT_ACCESS.getCode());
        }
        else  if (auditQuestionParam.getQuestionStatus().equals(QuestionStatus.AUDIT_FAILURE.getCode()))
        {
            systemMsg.setMsgType(SysMsgType.QUESTION_AUDIT_NOT_ACCESS.getCode());
        }
        else throw new QuestionStatusException();
        systemMsg.setFromUserId(0);
        systemMsg.setToUserId(question.getUserId());
        systemMsgMapper.insert(systemMsg);
        question.setQuestionStatus(auditQuestionParam.getQuestionStatus());
        questionMapper.updateById(question);
        return SaResult.ok().setMsg("审核成功");
    }

    @Autowired
    AnswerMapper answerMapper;
    @Autowired
    UserOpMapper userOpMapper;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public QuestionVoToUser getQuestionInfoById(Integer questionId)
    {
        Question q = questionMapper.selectById(questionId);
        return voService.questionVoToUser(q);
    }
    /**
     * questionList转化成InfoList
     * @param questions
     * @return
     */
    public List<QuestionVoToUser> questionToQuestionInfoLIst(List<Question> questions)
    {
        return questions.stream().map(voService::questionVoToUser).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<QuestionVoToUser> getAllMyQuestion(int userId)
    {
        List<Question> questions = questionMapper.selectList(new QueryWrapper<Question>().eq("user_id", userId));
        return questionToQuestionInfoLIst(questions);
    }

    @Autowired
    UserOpService userOpService;

    @Transactional(rollbackFor=Exception.class)
    public void likeQuestion(int userId, Question question, Integer status)
    {
        systemMsgService.addLikeQuestionMsg(userId,question);
        userOpService.addOp(UserOpType.LIKE_QUESTION,userId,question.getId(),status);
    }

    @Autowired
    QuestionAndAnswerRepository questionAndAnswerRepository;
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page
            <Question>
    searchQuestion(
            Integer pageNum,Integer pageSize
            , String keyword)
    {
//        int pageNum = Integer.parseInt(String.valueOf(resPage.getCurrent()));
//        int pageSize = Integer.parseInt(String.valueOf(resPage.getSize()));
//
//        log.info("pageNum:{},pageSize:{}",pageNum,pageSize);
        Pageable page= PageRequest.of(
                pageNum,pageSize
        );
        com.baomidou.mybatisplus.extension.plugins.pagination.Page
                <Question> resPage=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        Page<QuestionAndAnswerDoc> esPage = questionAndAnswerRepository.findQuestionAndAnswerByQuestionDescOrQuestionTitle(keyword, keyword, page);
        List<Integer> questionIds = esPage.getContent().stream().map(QuestionAndAnswerDoc::getQuestionId).collect(Collectors.toList());
        if (questionIds.isEmpty())
            resPage.setTotal(0);
        else {
            resPage.setRecords( questionMapper.selectBatchIds(questionIds));
            resPage.setTotal(esPage.getTotalElements());
        }
        return resPage;
    }

    public Integer getQuestionNum(Date startTime, Date endTime)
    {
        return questionMapper.selectCount(
                new QueryWrapper<Question>()
                .ge("create_time",startTime)
                .le("create_time",endTime)
        );
    }

    @Autowired
    UserConsultAreaMapper userConsultAreaMapper;
    @Autowired
    ConsultAreaMapper consultAreaMapper;
    public PageRes<QuestionAndAnswer> recommendQAToUser(@NotNull(message = "分页数不能为空") Integer pageNum, @NotNull(message = "分页大小不能为空") Integer pageSize, int userId)
    {
        log.info("推荐问答给用户");
        PageRes<QuestionAndAnswer> res = new PageRes<>();
        List<String> likeAreaIdList = userConsultAreaMapper.selectList(new QueryWrapper<UserConsultArea>().eq("user_id", userId))
                .stream().map(UserConsultArea::getAreaId).collect(Collectors.toList());
        if (likeAreaIdList.isEmpty())
        {
            log.info("用户未设置关注领域,推荐全部领域");
            likeAreaIdList=
                    consultAreaMapper.selectList(null).stream().map(ConsultArea::getId).collect(Collectors.toList());
        }
        if (!likeAreaIdList.isEmpty())
        {
            log.info("感兴趣的领域:{}",likeAreaIdList);
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Integer> questionPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
            questionMapper.pageQuestionIdByAreaId(
                    questionPage,likeAreaIdList
            );
            log.info("搜索到感兴趣的问题id:{},total:{}",questionPage.getRecords(),questionPage.getTotal());
            res.setTotal(questionPage.getTotal());
            res.setList(questionPage.getRecords().stream().map(
                    qId->{
                        Question q = questionMapper.selectById(qId);
                        if (q==null)throw new QuestionIdInvalidException("推荐问题时出现问题id无效",qId);
                        Integer topAnswerId = voService.mostPopularAnswerIdQuestion(q.getId());
                        Answer topAnswer = answerMapper.selectById(topAnswerId);
                        QuestionAndAnswer qa = new QuestionAndAnswer();
                        BeanUtils.copyProperties(q,qa);
                        qa.setAnswer(voService.answerVoToUser(topAnswer));
                        qa.setConsultAreaList(voService.questionAreaList(q.getId()));
                        log.info("推荐问答{}",qa);
                        return qa;
                    }
            ).collect(Collectors.toList()));
        }
        return res;
    }
}
