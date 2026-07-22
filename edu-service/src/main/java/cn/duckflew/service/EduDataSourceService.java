package cn.duckflew.service;

import cn.duckflew.entity.UniversityMajor;
import cn.duckflew.entity.professor.EduDataSource;
import cn.duckflew.entity.professor.EduExperience;
import cn.duckflew.exception.CardIdExistException;
import cn.duckflew.exception.EduDataSourceIdInvalidException;
import cn.duckflew.mapper.*;
import cn.duckflew.vo.EduDataSourceVo;
import cn.duckflew.vo.EduExperienceVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Service
public class EduDataSourceService extends ServiceImpl<EduDataSourceMapper, EduDataSource>
{
    @Autowired
    EduDataSourceMapper eduDataSourceMapper;
    @Autowired
    EduExperienceMapper eduExperienceMapper;
    @Autowired
    UniversityMapper universityMapper;
    @Autowired
    UniversityMajorMapper universityMajorMapper;
    @Autowired
    DegreeMapper degreeMapper;

    @Autowired
    ResearchDirectionMapper researchDirectionMapper;
    public EduExperienceVo eduExperienceVo(EduExperience eduExperience)
    {
        EduExperienceVo res = new EduExperienceVo();
        BeanUtils.copyProperties(eduExperience,res);
        if (eduExperience.getDegreeId()!=null)
            res.setDegree(degreeMapper.selectById(eduExperience.getDegreeId()));
        if (eduExperience.getSchoolId()!=null)
            res.setUniversity(universityMapper.selectById(eduExperience.getSchoolId()));
        if (eduExperience.getMajorId()!=null)
            res.setMajor(universityMajorMapper.selectById(eduExperience.getMajorId()));
        return res;
    }
    public EduDataSourceVo eduDataSourceVo(EduDataSource eduDataSource)
    {
        EduDataSourceVo res = new EduDataSourceVo();
        if (eduDataSource==null)
        {
            return null;
        }
        BeanUtils.copyProperties(eduDataSource,res);
        if (eduDataSource.getId()!=null)
        {
            List<EduExperience> eduExperienceList = eduExperienceMapper.selectList(new QueryWrapper<EduExperience>().eq("edu_datasource_id", eduDataSource.getId()));
            if (!eduExperienceList.isEmpty())
            {
                res.setEduExperienceList(
                        eduExperienceList.stream().map(
                                this::eduExperienceVo
                        ).collect(Collectors.toList())
                );
            }
        }
        return res;
    }

    public void checkCardIdRepeat(String cardId)
    {
        EduDataSource res = eduDataSourceMapper.selectOne(new QueryWrapper<EduDataSource>().eq("card_id", cardId));
        if (res!=null)
        {
            throw new CardIdExistException("已经存在此人的数据源",cardId);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public Integer add(EduDataSourceVo eduDataSource)
    {
        checkCardIdRepeat(eduDataSource.getCardId());
        EduDataSource toInsert = new EduDataSource();
        BeanUtils.copyProperties(eduDataSource,toInsert);
        eduDataSourceMapper.insert(toInsert);
        eduDataSource.getEduExperienceList()
                .forEach(experienceVo->{
                    EduExperience res = new EduExperience();
                    BeanUtils.copyProperties(experienceVo,res);
                    res.setEduDatasourceId(toInsert.getId());
                    eduExperienceMapper.insert(res);
                });
        return toInsert.getId();
    }

    public EduDataSource getById(Integer id)
    {
        EduDataSource res = eduDataSourceMapper.selectById(id);
        if (res==null)
            throw new EduDataSourceIdInvalidException("数据源id无效",id);
        return res;
    }
    @Transactional(rollbackFor = Exception.class)
    public void addExperienceToDatasource( EduExperience eduExperience)
    {
        getById(eduExperience.getEduDatasourceId());
        eduExperienceMapper.insert(eduExperience);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer datasourceId)
    {
        eduDataSourceMapper.deleteById(datasourceId);
        eduExperienceMapper.delete(
                new QueryWrapper<EduExperience>()
                .eq("edu_datasource_id",datasourceId)
        );
    }
}
