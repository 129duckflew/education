package cn.duckflew.service;

import cn.duckflew.entity.ConsultFeedback;
import cn.duckflew.entity.Question;
import cn.duckflew.entity.professor.Answer;
import cn.duckflew.entity.system.ChatMsg;
import cn.duckflew.entity.system.ChatWindow;
import cn.duckflew.entity.system.SystemMsg;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.enums.QuestionStatus;
import cn.duckflew.enums.SysMsgType;
import cn.duckflew.exception.*;
import cn.duckflew.mapper.*;
import cn.duckflew.vo.ChatInfo;
import cn.duckflew.vo.TypeNotReadCount;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class SystemMsgService extends ServiceImpl<SystemMsgMapper, SystemMsg>
{

    @Autowired
    BaseUserService baseUserService;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    AnswerService answerService;
    @Autowired
    ChatMsgService chatMsgService;
    @Autowired
    ConsultFeedbackMapper consultFeedbackMapper;
    public Map<String,Object> msgLoadData(@NotNull(message = "消息不能为空") SystemMsg msg)
    {
        Map<String,Object> res=new HashMap<>();
        res.put("msg",msg);
        if (msg.getMsgType().equals(SysMsgType.PRIVATE_CHAT.getCode()))
        {
            BaseUser fromUser = baseUserService.getById(msg.getFromUserId());
            if (fromUser==null)throw new BaseUserNotExistException();
            ChatMsg chatMsg = chatMsgService.getById(msg.getId());
            res.put("fromUserRealName",fromUser.getRealName());
            res.put("fromUserAvatar",fromUser.getAvatar());
            res.put("fromUserId",msg.getFromUserId());
            res.put("chatContent",chatMsg.getChatMsgContent());
        }
        else if (msg.getMsgType().equals(SysMsgType.GET_ANSWER.getCode()))
        {
            Answer answer = answerService.getById(msg.getResourceId());
            if (answer==null)throw new AnswerNotExistException("load收到回答消息附加信息时出现回答id无效",msg.getResourceId());
            Question question = questionMapper.selectById(answer.getQuestionId());
            if (question==null)throw new QuestionIdInvalidException("load收到回答消息附加信息时出现问题id无效",answer.getQuestionId());
            res.put("questionTitle",question.getQuestionTitle());
            res.put("questionId",question.getId());
            res.put("answerId",answer.getId());
            if (answer.getAnswerText().length()>10)
                res.put("answerSummary",answer.getAnswerText().substring(0,10));
            else
                res.put("answerSummary",answer.getAnswerText());
            BaseUser relatedUser = baseUserService.getById(msg.getRelatedUserId());
            if (relatedUser==null)throw new BaseUserNotExistException();
            res.put("professorName",relatedUser.getRealName());
            res.put("professorAvatar",relatedUser.getAvatar());
            res.put("professorId",msg.getRelatedUserId());
        }
        else if (msg.getMsgType().equals(SysMsgType.GET_QUESTION.getCode()))
        {
            Question question = questionMapper.selectById(msg.getResourceId());
            BaseUser relatedUser = baseUserService.getById(msg.getRelatedUserId());
            if (question==null)throw new QuestionIdInvalidException("生成收到提问消息时出现问题id不存在",msg.getResourceId());
            res.put("questionTitle",question.getQuestionTitle());
            res.put("questionId",question.getId());
            if (relatedUser==null)
            {
                log.error("生成收到提问消息时出现关联用户id无效,relatedUserId={},msgId={},msgToUserId={}",msg.getRelatedUserId(),msg.getId(),msg.getToUserId());
                throw new BaseUserNotExistException();
            }
            res.put("consultUserName",relatedUser.getRealName());
            res.put("consultUserAvatar",relatedUser.getAvatar());
            res.put("consultUserId",msg.getRelatedUserId());
        }
        else if (msg.getMsgType().equals(SysMsgType.LIKE_ANSWER.getCode()))
        {
            BaseUser relatedUser = baseUserService.getById(msg.getRelatedUserId());
            if (relatedUser==null)throw new BaseUserNotExistException();
            res.put("fromUserRealName",relatedUser.getRealName());
            res.put("fromUserAvatar",relatedUser.getAvatar());
            res.put("fromUserId",msg.getRelatedUserId());
            Answer ans = answerService.getById(msg.getResourceId());
            if (ans==null)throw new AnswerNotExistException("装在回答点赞消息附加数据时出现问题id无效",msg.getResourceId());
            res.put("answerId",ans.getId());
            if (ans.getAnswerText().length()>10)
            res.put("answerSummary",ans.getAnswerText().substring(0,10));
            else  res.put("answerSummary",ans.getAnswerText());
        }
        else if (msg.getMsgType().equals(SysMsgType.LIKE_QUESTION.getCode()))
        {
            BaseUser relatedUser = baseUserService.getById(msg.getRelatedUserId());
            if (relatedUser==null)throw new BaseUserNotExistException();
            res.put("fromUserRealName",relatedUser.getRealName());
            res.put("fromUserAvatar",relatedUser.getAvatar());
            res.put("fromUserId",msg.getRelatedUserId());
            Question question = questionMapper.selectById(msg.getResourceId());
            if (question==null)throw new QuestionIdInvalidException("load问题点赞消息数据时出现问题id无效",msg.getResourceId());
            res.put("questionTitle",question.getQuestionTitle());
            res.put("questionId",question.getId());
        }
        else if (msg.getMsgType().equals(SysMsgType.BAN_QUESTION.getCode()))
        {
            Question question = questionMapper.selectById(msg.getResourceId());
            if (question==null)throw new QuestionIdInvalidException("load封禁问题消息的附加数据出现问题id无效",msg.getResourceId());
            res.put("questionTitle",question.getQuestionTitle());
            res.put("questionId",question.getId());
        }
        else if (msg.getMsgType().equals(SysMsgType.BAN_ANSWER.getCode()))
        {
            Answer ans = answerService.getById(msg.getResourceId());
            if (ans==null)throw new AnswerNotExistException("load封禁回答消息附加数据时候出现回答id无效",msg.getResourceId());
            res.put("answerId",ans.getId());
            if (ans.getAnswerText().length()>10)
            res.put("answerSummary",ans.getAnswerText().substring(0,10));
            else res.put("answerSummary",ans.getAnswerText());
        }
        else if (msg.getMsgType().equals(SysMsgType.GET_EVALUATION.getCode()))
        {
            ConsultFeedback cf = consultFeedbackMapper.selectById(msg.getResourceId());
            if (cf==null)throw new ConsultFeedbackIdInvalidException("load收到评价消息附加数据时候出现评价id无效",msg.getResourceId());
            res.put("evaluationId",cf.getId());
            BaseUser relatedUser = baseUserService.getById(msg.getRelatedUserId());
            if (relatedUser==null)throw new BaseUserNotExistException();
            res.put("fromUserRealName",relatedUser.getRealName());
            res.put("fromUserAvatar",relatedUser.getAvatar());
            res.put("fromUserId",msg.getRelatedUserId());
        }
        else if (msg.getMsgType().equals(SysMsgType.QUESTION_AUDIT_ACCESS.getCode()))
        {
            Question question = questionMapper.selectById(msg.getResourceId());
            if (question==null)throw new QuestionIdInvalidException("load问题通过审核时出现questionId无效",msg.getResourceId());
            res.put("questionTitle",question.getQuestionTitle());
            res.put("questionId",question.getId());
        }
        else if (
                msg.getMsgType().equals(SysMsgType.QUESTION_AUDIT_NOT_ACCESS.getCode())
        )
        {
            Question question = questionMapper.selectById(msg.getResourceId());
            if (question==null)throw new QuestionIdInvalidException("load问题未通过审核消息时出现questionId无效",msg.getResourceId());
            res.put("questionTitle",question.getQuestionTitle());
            res.put("questionId",question.getId());
        }
        return res;
//        else if (msg.getMsgType().equals(SysMsgType.QUESTION_AUDIT_ACCESS.getCode()))
//        {
//            Answer ans = answerService.getById(msg.getResourceId());
//            if (ans==null)throw new AnswerNotExistException();
//            res.put("answerId",ans.getId());
//            res.put("answerSummary",ans.getAnswerText().substring(0,10));
//        }
    }
    @Autowired
    SystemMsgMapper systemMsgMapper;
    public boolean setIsRead(Integer msgId,Integer toUserId)
    {
        SystemMsg msg=systemMsgMapper.selectOne(
                new QueryWrapper<SystemMsg>()
                        .eq("id",msgId)
                        .eq("to_user_id",toUserId)
        );
        if (msg==null)return false;
        msg.setIsRead(1);
        systemMsgMapper.updateById(msg);
        return true;
    }



    @Transactional(rollbackFor = Exception.class)
    public void  chat(Integer fromUserId,String chatMsgContent,Integer toUserId)
    {
        SystemMsg baseMsg = SystemMsg.baseMsg();
        baseMsg.setFromUserId(fromUserId);
        baseMsg.setToUserId(toUserId);
        baseMsg.setMsgType(SysMsgType.PRIVATE_CHAT.getCode());
        try
        {
            systemMsgMapper.insert(baseMsg);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new ChatMsgSendFailureException();
        }
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setId(baseMsg.getId());
        chatMsg.setChatMsgContent(chatMsgContent);
        chatWindowService.addWindow(fromUserId,toUserId);
        chatWindowService.addWindow(toUserId,fromUserId);
        chatMsgService.save(chatMsg);
    }

    @Autowired
    BaseUserMapper userMapper;
    public Integer getChatNotReadNum(Integer fromUserId,Integer toUserId)
    {
        return systemMsgMapper.selectCount(
                new QueryWrapper<SystemMsg>()
                .eq("from_user_id",fromUserId)
                .eq("to_user_id",toUserId)
                .eq("msg_type",SysMsgType.PRIVATE_CHAT.getCode())
                .eq("is_read",0)
        );
    }
    @Autowired
    ChatWindowService chatWindowService;
    public List<ChatInfo> getChatList(Integer userId)
    {
        List<ChatWindow> windowList = chatWindowService.list(new QueryWrapper<ChatWindow>().eq("from_user_id", userId));
        List<ChatInfo>res=windowList.stream().map(window->{
            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setToUserId(window.getToUserId());
            BaseUser chatUser = userMapper.selectById(window.getToUserId());
            chatInfo.setToUserName(chatUser.getRealName());
            chatInfo.setToUserAvatar(chatUser.getAvatar());
            chatInfo.setNotReadNum(getChatNotReadNum(window.getToUserId(),userId));
            return chatInfo;
        }).collect(Collectors.toList());
        return res;
    }

    public List<Map<String, Object>> getMsgRecordList(Integer fromUserId,Integer toUserId)
    {
        List<SystemMsg> msgList=systemMsgMapper.getRecordList(fromUserId,toUserId);
        return msgList.stream().map(this::msgLoadData).collect(Collectors.toList());
    }

    public void generateBanQuestionMsg(Integer qId)
    {
        Question question = questionMapper.selectById(qId);
        if (question==null)throw new QuestionIdInvalidException("生成ban问题消息出现问题id无效",qId);
        question.setQuestionStatus(QuestionStatus.FORBIDDEN.getCode());
        SystemMsg banQuestionMsg = SystemMsg.baseMsg();
        banQuestionMsg.setResourceId(qId);
        banQuestionMsg.setMsgType(SysMsgType.BAN_QUESTION.getCode());
        banQuestionMsg.setToUserId(question.getUserId());
        questionMapper.updateById(question);
        save(banQuestionMsg);
    }

    public void generateBanAnswerMsg(Integer ansId)
    {
        Answer answer = answerService.getById(ansId);
        if (answer==null)throw new AnswerNotExistException("生成封禁回答消息时出现回答id无效",ansId);
        answer.setAnswerStatus(QuestionStatus.FORBIDDEN.getCode());
        SystemMsg banQuestionMsg = SystemMsg.baseMsg();
        banQuestionMsg.setResourceId(ansId);
        banQuestionMsg.setMsgType(SysMsgType.BAN_ANSWER.getCode());
        banQuestionMsg.setToUserId(answer.getProfessorId());
        answerService.updateById(answer);
        save(banQuestionMsg);
    }
    public void generateGetEvaMsg(Integer cfId)
    {
        ConsultFeedback cf = consultFeedbackMapper.selectById(cfId);
        if (cf==null)throw new AnswerNotExistException("生成收到消息时出评价id无效",cfId);
        SystemMsg getEvaMsg = SystemMsg.baseMsg();
        getEvaMsg.setResourceId(cfId);
        getEvaMsg.setMsgType(SysMsgType.GET_EVALUATION.getCode());
        getEvaMsg.setToUserId(cf.getProfessorId());
        getEvaMsg.setRelatedUserId(cf.getUserId());
        getEvaMsg.setFromUserId(0);
        save(getEvaMsg);
    }

    public Map<String, Object> allTypeNotReadCount(int userId)
    {
        Map<String,Object> res=new HashMap<>();
        List<TypeNotReadCount>list=new ArrayList<>();
        int total=0;
        for (int i = 0; i < 12; i++)
        {
            if (i==3)continue;
            Integer count=systemMsgMapper.selectCount(
                    new QueryWrapper<SystemMsg>()
                    .eq("to_user_id",userId)
                    .eq("is_read",0)
                    .eq("msg_type",i)
            );
            total+=count;
            list.add(   new TypeNotReadCount(i,count));
            res.put("list",list);
        }
        res.put("total",total);
        return res;
    }

    public void addGetQuestionMsg(Integer professorId,Question question)
    {
        /**
         * 生成消息体
         */
        log.info("生成消息: 用户{}收到提问,提问人:{}",professorId,question.getUserId());
        SystemMsg systemMsg =  SystemMsg.baseMsg();
        systemMsg.setToUserId(professorId);
        systemMsg.setFromUserId(0);
        systemMsg.setMsgType(SysMsgType.GET_QUESTION.getCode());
        systemMsg.setResourceId(question.getId());
        systemMsg.setRelatedUserId(question.getUserId());
        systemMsgMapper.insert(systemMsg);

    }

    public void addLikeQuestionMsg(int userId, Question question)
    {
        SystemMsg systemMsg = SystemMsg.baseMsg();
        systemMsg.setMsgType(SysMsgType.LIKE_QUESTION.getCode());
        systemMsg.setRelatedUserId(userId);
        systemMsg.setResourceId(question.getId());
        systemMsg.setToUserId(question.getUserId());
        systemMsg.setFromUserId(0);
        systemMsgMapper.insert(systemMsg);
    }
}
