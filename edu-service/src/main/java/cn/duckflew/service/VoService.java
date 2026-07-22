package cn.duckflew.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.duckflew.document.QuestionAndAnswerDoc;
import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.Question;
import cn.duckflew.entity.QuestionArea;
import cn.duckflew.entity.QuestionProfessor;
import cn.duckflew.entity.pay.Order;
import cn.duckflew.entity.professor.Answer;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.entity.user.UserOp;
import cn.duckflew.enums.UserOpType;
import cn.duckflew.exception.PaidQuestionException;
import cn.duckflew.exception.QuestionIdInvalidException;
import cn.duckflew.mapper.*;
import cn.duckflew.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 专门用来处理entity->vo的service
 *  例如给entity增强字段  亦或是附加vo属性
 */
@Service

@Slf4j
public class VoService
{
    @Autowired
    BaseUserMapper baseUserMapper;
    @Autowired
    UserOpMapper userOpMapper;

    @Autowired
    AnswerMapper answerMapper;

    @Autowired
    QuestionProfessorMapper questionProfessorMapper;
    @Autowired
    QuestionAreaMapper questionAreaMapper;
    /**
     * order转付费问题vo
     * @param order
     * @return
     */
    public PaidQuestionOrderVo orderToVo(Order order)
    {

        log.info("order转付费问题vo,order:{}",order);
        PaidQuestionOrderVo res = new PaidQuestionOrderVo();
        BeanUtils.copyProperties(order,res);
        Question question = questionMapper.selectOne(new QueryWrapper<Question>().eq("order_id", order.getId()));
        if (question==null)throw new QuestionIdInvalidException("通过订单无法查询到问题信息",-1);
        List<QuestionProfessor> qpList = questionProfessorMapper.selectList(new QueryWrapper<QuestionProfessor>().eq("question_id", question.getId()));
        if (qpList==null||qpList.size()>1)throw new PaidQuestionException("付费问题数据错误,提问教授不存在或Size>1,订单id",order.getId());
        BaseUser professor = baseUserMapper.selectById(qpList.get(0).getProfessorId());
        res.setProfessorName(professor.getRealName());
        List<String> areaIdList = questionAreaMapper.selectList(new QueryWrapper<QuestionArea>().eq("question_id", question.getId())).stream().map(QuestionArea::getConsultAreaId).collect(Collectors.toList());
        res.setAreaList(consultAreaMapper.selectBatchIds(areaIdList));
        return res;
    }
    /**
     * 获取我是否点赞了问题
     * @param questionId
     * @return
     */
    public boolean iLikeStatusOfQuestion(Integer questionId)
    {
        if (!StpUtil.isLogin())return false;
        Integer userId=StpUtil.getLoginIdAsInt();
        UserOp uo=userOpMapper.selectOne(
                new QueryWrapper<UserOp>()
                        .eq("op_type",UserOpType.LIKE_QUESTION.getCode())
                        .eq("op_status",1)
                        .eq("resource_id",questionId)
                        .eq("user_id",userId)
        );
        return uo!=null;
    }

    /**
     * 根据问题id获取点赞数
     * @param questionId
     * @return
     */
    public Integer getAnsNumOfQuestionById(Integer questionId)
    {
        return answerMapper.selectCount(
                new QueryWrapper<Answer>()
                        .eq("question_id",questionId)
        );
    }
    @Autowired
    QuestionMapper questionMapper;

    public QuestionVoToUser questionVoToUser(Question question)
    {
        QuestionVoToUser res = new QuestionVoToUser();
        BeanUtils.copyProperties(question,res);
        res.setAnsNum(getAnsNumOfQuestionById(question.getId()));
        res.setLikeNum(getLikeNumOfQuestion(question.getId()));
        res.setILike(iLikeStatusOfQuestion(question.getId()));
        res.setConsultAreaList(questionAreaList(question.getId()));
        return res;
    }
    public List<ConsultArea> questionAreaList(Integer questionId){
        List<String> areaIdList = questionAreaMapper.selectList(new QueryWrapper<QuestionArea>().eq("question_id", questionId)).stream().map(QuestionArea::getConsultAreaId).collect(Collectors.toList());
        if (areaIdList.isEmpty())return null;
        return consultAreaMapper.selectBatchIds(areaIdList);
    }


    public AnswerVoToAdmin answerVoToAdmin(Answer answer)
    {
        AnswerVoToAdmin res = new AnswerVoToAdmin();
        BeanUtils.copyProperties(answer,res);
        Question question = questionMapper.selectById(answer.getQuestionId());
        res.setQuestion(question);
        res.setLikeNum(getLikeNumOfAnsById(answer.getId()));
        res.setCollectNum(getCollectNumOfAnsById(answer.getId()));
        BaseUser author = baseUserMapper.selectById(answer.getProfessorId());
        res.setAuthor(author);
        return res;
    }
    /**
     * 获取问题的点赞数
     * @param questionId
     * @return
     */
    public Integer getLikeNumOfQuestion(Integer questionId)
    {
        return userOpMapper.selectCount(
                new QueryWrapper<UserOp>()
                        .eq("op_type",UserOpType.LIKE_QUESTION.getCode())
                        .eq("op_status",1)
                        .eq("resource_id",questionId)
        );
    }
    public AnswerVoToUser answerVoToUser(Answer ans)
    {
        if (ans==null)return null;
        String realName = baseUserMapper.selectById(ans.getProfessorId()).getRealName();
        AnswerVoToUser answerVoToUser = new AnswerVoToUser(
                ans,
                getLikeNumOfAnsById(ans.getId()),
                getCollectNumOfAnsById(ans.getId()),
                realName,
                getILikeStatusOfAns(ans.getId()),
                getICollectStatusOfAns(ans.getId()));
        return answerVoToUser;
    }

    @Autowired
    ConsultAreaMapper consultAreaMapper;
    public List<ConsultArea> getChildrenByParentId(String parentId)
    {
        return consultAreaMapper.selectList(
                new QueryWrapper<ConsultArea>()
                .likeRight("id",parentId)
        );
    }
    public List<String> getChildrenIdByParentId(String parentId)
    {
        return getChildrenByParentId(parentId).stream().map(ConsultArea::getId).collect(Collectors.toList());
    }
    public Set<String> areaIdSet(List<String> ids)
    {
        Set<String> res=new HashSet<>();
        ids.forEach(id->res.addAll(getChildrenIdByParentId(id)));
        return res;
    }

    /**
     * 根据ans id获取我是否收藏了这个回答
     * @param ansId
     * @return
     */
    public boolean getICollectStatusOfAns(Integer ansId)
    {
        if (!StpUtil.isLogin())return false;
        Integer userId= StpUtil.getLoginIdAsInt();

        UserOp uo= userOpMapper.selectOne(
                new QueryWrapper<UserOp>()
                        .eq("op_type",UserOpType.COLLECT_ANSWER.getCode())
                        .eq("op_status",1)
                        .eq("user_id",userId)
                        .eq("resource_id",ansId)
        );
        return uo!=null;
    }

    /**
     * 根据id获取回答的点赞数
     * @param ansId
     * @return
     */
    public Integer getLikeNumOfAnsById(Integer ansId)
    {
        return userOpMapper.selectCount(
                new QueryWrapper<UserOp>()
                        .eq("op_type",UserOpType.LIKE_ANSWER.getCode())
                        .eq("op_status",1)
                        .eq("resource_id",ansId)
        );
    }
    /**
     * 根据id获取回答的收藏数
     * @param ansId
     * @return
     */
    public Integer getCollectNumOfAnsById(Integer ansId)
    {
        return userOpMapper.selectCount(
                new QueryWrapper<UserOp>()
                        .eq("op_type", UserOpType.COLLECT_ANSWER.getCode())
                        .eq("op_status",1)
                        .eq("resource_id",ansId)
        );
    }

    /**
     * 根据ans id获取我是否点赞了回答
     * @param ansId
     * @return
     */
    public boolean getILikeStatusOfAns(Integer ansId)
    {
        if (!StpUtil.isLogin())return false;
        Integer userId= StpUtil.getLoginIdAsInt();

        UserOp uo= userOpMapper.selectOne(
                new QueryWrapper<UserOp>()
                        .eq("op_type",UserOpType.LIKE_ANSWER.getCode())
                        .eq("op_status",1)
                        .eq("user_id",userId)
                        .eq("resource_id",ansId)
        );
        return uo!=null;
    }

    public QuestionAndAnswer answerToQA(Answer answer)
    {
        QuestionAndAnswer res = new QuestionAndAnswer();
        Question q = questionMapper.selectById(answer.getQuestionId());
        BeanUtils.copyProperties(q,res);
        res.setConsultAreaList(        questionAreaList(q.getId()));
        return res;
    }
    public QuestionAndAnswerDoc answerToQADoc(Answer answer)
    {
        QuestionAndAnswerDoc res = new QuestionAndAnswerDoc();
        Question q = questionMapper.selectById(answer.getQuestionId());
        res.setAnswerId(answer.getId());
        res.setAnswerText(answer.getAnswerText());
        res.setProfessorName(baseUserMapper.selectById(answer.getProfessorId()).getRealName());
        res.setQuestionDesc(q.getQuestionDesc());
        res.setQuestionTitle(q.getQuestionTitle());
        res.setQuestionId(q.getId());
        res.setId(answer.getId());
        return res;
    }
    public Integer mostPopularAnswerIdQuestion(Integer questionId)
    {
        List<Integer> answerIdList = answerMapper.selectList(new QueryWrapper<Answer>().eq("question_id", questionId)).stream().map(Answer::getId).collect(Collectors.toList());
        Integer mostPopularId=0;
        if (!answerIdList.isEmpty())
        {
            Integer mostLikeNum=0;
            for (Integer answerId : answerIdList)
            {
                Integer curLikeNum = getLikeNumOfAnsById(answerId);
                if (curLikeNum>=mostLikeNum)
                {
                    mostLikeNum=curLikeNum;
                    mostPopularId=answerId;
                }
            }
        }
        return  mostPopularId;
    }
}
