package cn.duckflew.service;

import cn.duckflew.document.ProfessorInfoDoc;
import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.ProInfo;
import cn.duckflew.entity.ProfessorAnswerArea;
import cn.duckflew.entity.professor.JobRank;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.exception.NotProfessorException;
import cn.duckflew.mapper.BaseUserMapper;
import cn.duckflew.mapper.ConsultAreaMapper;
import cn.duckflew.mapper.JobRankMapper;
import cn.duckflew.mapper.ProfessorAnswerMapper;
import cn.duckflew.mapper.admin.ProInfoMapper;
import cn.duckflew.vo.ProIntroduction;
import cn.duckflew.vo.ProfessorInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProInfoService extends ServiceImpl<ProInfoMapper, ProInfo>
{
    @Autowired
    ProInfoMapper proInfoMapper;
    @Autowired
    BaseUserMapper baseUserMapper;
    @Autowired
    JobRankMapper jobRankMapper;
    @Autowired
    ProfessorAnswerAreaService professorAnswerAreaService;
    @Autowired
    ProfessorService professorService;

    @Autowired
    BaseUserService baseUserService;

    @Autowired
    ConsultAreaMapper consultAreaMapper;
    @Autowired
    ProfessorAnswerMapper userAnswerAreaMapper;

    @Autowired
    EsService  esService;
    @Transactional(rollbackFor =Exception.class)
    public void setIntroduction(int professorId, String introduction)
    {
        ProInfo proInfo = proInfoMapper.selectById(professorId);
        proInfo.setIntroduction(introduction);
        ProfessorInfoDoc doc = professorService.getProfessorDoc(baseUserMapper.selectById(professorId));
        esService.saveProfessorInfoDoc(doc);
        proInfoMapper.updateById(proInfo);
    }

    /**
     * 修改问答价格
     * @param professorId
     * @param price
     */
    @Transactional(rollbackFor =Exception.class)
    public void setPrice(int professorId, BigDecimal price)
    {
        ProInfo proInfo = proInfoMapper.selectById(professorId);
        proInfo.setConsultPrice(price);
        proInfoMapper.updateById(proInfo);
    }
}
