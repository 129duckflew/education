package cn.duckflew.service;

import cn.duckflew.entity.ConsultFeedback;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.mapper.ConsultFeedbackMapper;
import cn.duckflew.vo.ConsultFeedbackVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ConsultFeedbackService extends ServiceImpl<ConsultFeedbackMapper,ConsultFeedback>
{
    @Autowired
    ConsultFeedbackMapper consultFeedbackMapper;
    @Autowired
    ProfessorService professorService;

    @Autowired
    SystemMsgService systemMsgService;
    @Transactional(rollbackFor = Exception.class)
    public void addConsultFeedback(Integer userId, Integer professorId, String evaluation, Integer star)
    {
        professorService.getProfessorById(professorId);
        ConsultFeedback cf = new ConsultFeedback();
        cf.setCreateTime(new Date());
        cf.setUserId(userId);
        cf.setEvaluation(evaluation);
        cf.setStar(star);
        cf.setProfessorId(professorId);
        consultFeedbackMapper.insert(cf);
        systemMsgService.generateGetEvaMsg(cf.getId());
        log.info("用户{}发表评论{}",userId,evaluation);
    }

    public ConsultFeedbackVo loadVo(ConsultFeedback cf)
    {
        BaseUser pro = professorService.getProfessorById(cf.getProfessorId());
        ConsultFeedbackVo consultFeedbackVo = new ConsultFeedbackVo(cf);
        consultFeedbackVo.setUserNickName(pro.getNickName());
        return consultFeedbackVo;
    }


}
