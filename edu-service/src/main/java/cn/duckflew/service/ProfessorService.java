package cn.duckflew.service;

import cn.duckflew.document.ProfessorInfoDoc;
import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.ProInfo;
import cn.duckflew.entity.ProfessorAnswerArea;
import cn.duckflew.entity.professor.JobRank;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.enums.UserType;
import cn.duckflew.exception.*;
import cn.duckflew.mapper.*;
import cn.duckflew.mapper.admin.ProInfoMapper;
import cn.duckflew.repo.ProfessorInfoRepository;
import cn.duckflew.vo.PageRes;
import cn.duckflew.vo.ProfessorInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProfessorService
{

    @Autowired
    ProInfoMapper proInfoMapper;

    @Autowired
    JobRankMapper jobRankMapper;
    @Autowired
    ProfessorAnswerAreaMapper professorAnswerAreaMapper;

    /**
     * 教授用户转Vo
     * @param baseUser
     * @return
     */
    public ProfessorInfo professorInfo(BaseUser baseUser)
    {
        if (baseUser.getUserType().equals(UserType.GUEST.getCode()))
            throw new NotPreProfessorException("此用户还未盛情教授",baseUser.getId());
        ProfessorInfo res = new ProfessorInfo();
        BeanUtils.copyProperties(baseUser,res);
        ProInfo proInfo = proInfoMapper.selectById(baseUser.getId());
        res.setProInfo(proInfo);
        if (proInfo.getJobRankId()!=null)
        {
            JobRank jobRank = jobRankMapper.selectById(proInfo.getJobRankId());
            res.setJobRank(jobRank);
        }
        List<String>areaIds=professorAnswerAreaMapper.selectList(
                new QueryWrapper<ProfessorAnswerArea>()
                .eq("professor_id",baseUser.getId())
        ).stream().map(ProfessorAnswerArea::getConsultAreaId).collect(Collectors.toList());
        if (!areaIds.isEmpty())res.setAnswerAreaList(
                consultAreaMapper.selectBatchIds(areaIds)
        );
        return res;
    }
    public BaseUser getProfessorById(Integer professorId)
    {
        BaseUser pro = baseUserMapper.selectById(professorId);
        if (pro.getUserType().equals(UserType.PROFESSOR.getCode()))
        {
            return pro;
        }
        log.error("id不是教授的id");
        throw new ProfessorIdInvalidException("无此id的教授",professorId);
    }


    @Autowired
    ProfessorAnswerMapper professorAnswerMapper;
    @Autowired
    EsService esService;
    @Autowired
    ConsultAreaService consultAreaService;
    @Transactional(rollbackFor = Exception.class)
    public void deleteOldAnswerArea(Integer professorId)
    {
        getProfessorById(professorId); //验证是否professorId
        professorAnswerMapper.delete(new QueryWrapper<ProfessorAnswerArea>()
        .eq("professor_id",professorId));
    }
    @Transactional(rollbackFor = Exception.class)
    public void  setAnswerArea(int userId, List<String> consultAreaIds)
    {
        deleteOldAnswerArea(userId);
        log.info("删除旧answerArea");
        Set<String> areaIdSet=new HashSet<>();
        consultAreaIds.forEach(
                areaId-> areaIdSet.addAll(
                        consultAreaService.
                                getChildrenAndSelfByParentId(areaId).stream().map
                                (
                                        ConsultArea::getId).collect(Collectors.toList()
                        )
                )
        );
        for (String consultAreaId : areaIdSet)
        {
            ProfessorAnswerArea professorAnswerArea = new ProfessorAnswerArea();
            professorAnswerArea.setProfessorId(userId);
            professorAnswerArea.setConsultAreaId(consultAreaId);
            log.info("插入新answerArea To Mysql");
            professorAnswerMapper.insert(professorAnswerArea);
        }
        ProfessorInfoDoc doc = getProfessorDoc(getProfessorById(userId));
        esService.saveProfessorInfoDoc(doc);
        log.info("更新professorInfoDoc--->es");
    }

    @Autowired
    ConsultAreaMapper consultAreaMapper;

    @Autowired
    ProfessorAnswerAreaService professorAnswerAreaService;
    public List<ConsultArea> getAnswerAreaList(Integer professorId)
    {
        List<String> areaIdList = professorAnswerAreaMapper.selectList(new QueryWrapper<ProfessorAnswerArea>().eq("professor_id", professorId)).stream().map(ProfessorAnswerArea::getConsultAreaId).collect(Collectors.toList());

        if (areaIdList.isEmpty())return new ArrayList<>();
        return consultAreaMapper.selectBatchIds(
                areaIdList
        );
    }

    @Autowired
    BaseUserMapper baseUserMapper;
    public void accessProfessorReq(Integer userId)
    {
        BaseUser user = baseUserMapper.selectById(userId);
        if (user==null) throw new BaseUserNotExistException();
        user.setUserType(UserType.PROFESSOR.getCode());
        esService.saveProfessorInfoDoc(getProfessorDoc(user));
        log.info("教授申请被通过,新建proInfo存入数据库");
        ProInfo proInfo = new ProInfo();
        proInfo.setId(userId);
        proInfo.setConsultPrice(BigDecimal.ONE);
        baseUserMapper.updateById(user);
    }

    @Autowired
    ProfessorAnswerAreaService answerAreaService;
    @Autowired
    ProInfoService proInfoService;


    /**
     * 获取教授回答的领域
     * @param professorId
     * @return
     */
    public List<ConsultArea> professorAnswerAreaList(Integer professorId)
    {
        List<String> areaIdList = professorAnswerAreaMapper.selectList(new QueryWrapper<ProfessorAnswerArea>().eq("professor_id", professorId)).stream().map(ProfessorAnswerArea::getConsultAreaId).collect(Collectors.toList());
        if (areaIdList.isEmpty())return null;
        return consultAreaMapper.selectBatchIds(areaIdList);
    }
    /**
     * 根据教授信息生成EsDoc
     * @param professor
     * @returnsear
     */
    public ProfessorInfoDoc getProfessorDoc(BaseUser professor)
    {
        ProfessorInfoDoc res = new ProfessorInfoDoc();
        res.setProfessorId(professor.getId());
        res.setRealName(professor.getRealName());
        List<String> answerAreaNameList = professorAnswerAreaList(professor.getId()).stream().map(ConsultArea::getAreaName).collect(Collectors.toList());
        ProInfo proInfo = proInfoService.getById(professor.getId());
        if(proInfo==null)
            throw new ProInfoNotExistException("教授信息不存在",professor.getId());
        res.setProfessorIntroduction(proInfo.getIntroduction());
        res.setAnswerAreaNameList(answerAreaNameList);
        res.setAvatar(professor.getAvatar());
        JobRank jobRank = jobRankMapper.selectById(professor.getId());
        if (jobRank!=null)
        res.setJobRankName(jobRank.getJobRankName());
        return res;
    }

    public boolean isPro(int userId)
    {
        return baseUserMapper.selectById(userId).getUserType().equals( UserType.PROFESSOR.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addIntroduction(Integer proId, String introContent)
    {
        if (!isPro(proId))throw new NotProfessorException("此账号不是教授,无法添加教授简介",proId);
        ProInfo proInfo = proInfoMapper.selectById(proId);
        proInfo.setIntroduction(introContent);
        proInfoMapper.updateById(proInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelPro(Integer proId)
    {
        if (!isPro(proId))throw new NotProfessorException("此账号不是教授,无法添加教授简介",proId);
        BaseUser professor = baseUserMapper.selectById(proId);
        professor.setUserType(UserType.GUEST.getCode());
        baseUserMapper.updateById(professor);
        proInfoMapper.deleteById(proId);
    }

    public void setJobRankId(Integer proId, Integer jobRankId)
    {
        if (!isPro(proId))throw new NotProfessorException("此账号不是教授,无法添加职称",proId);
        ProInfo proInfo = proInfoMapper.selectById(proId);
        if (jobRankMapper.selectById(jobRankId)==null)
            throw new JobRankIdInvalidException("职称id无效",jobRankId);
        proInfo.setJobRankId(jobRankId);
        proInfoMapper.updateById(proInfo);
    }

    public ProfessorInfo professorInfo(Integer proId)
    {
        BaseUser pro = baseUserMapper.selectById(proId);
        if (pro==null)throw new ProfessorIdInvalidException("教授id无效",proId);
        return professorInfo(pro);
    }

    public PageRes<ProfessorInfo> getProfessorListByAreaIds(
            Integer pageNum,
            Integer  pageSize,
            List<String> areaIds
    )
    {
        if (areaIds.isEmpty()) return null;
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProfessorAnswerArea> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum,pageSize);
        professorAnswerAreaMapper.selectPage(
                page,
                new QueryWrapper<ProfessorAnswerArea>().in
                        ("consult_area_id", areaIds));
        Set<Integer> professorIds = page.getRecords().stream().map(ProfessorAnswerArea::getProfessorId).collect(Collectors.toSet());
        if (professorIds.isEmpty()){return null;}
        List<ProfessorInfo> list = professorIds.stream().map(id -> professorInfo(id)).collect(Collectors.toList());
        PageRes<ProfessorInfo> res = new PageRes<>();
        res.setList(list);
        res.setTotal(page.getTotal());
        return res;
    }
    @Autowired
    ProfessorInfoRepository professorInfoRepository;
    public PageRes<ProfessorInfo> searchProfessorByKeyword(Integer pageNum, Integer pageSize, String keyword)
    {
        Pageable pageable= PageRequest.of(pageNum,pageSize);
        Page<ProfessorInfoDoc> page = professorInfoRepository.findAllByRealNameOrProfessorIntroductionOrAnswerAreaNameListOrSchoolNameList(keyword, keyword, keyword, keyword, pageable);
        List<ProfessorInfo> list=
            page.getContent().stream().map(
                      doc-> professorInfo(doc.getProfessorId())
        ).collect(Collectors.toList());
        PageRes<ProfessorInfo> res = new PageRes<>();
        res.setTotal(page.getTotalElements());
        res.setList(list);
        return res;
    }
}
