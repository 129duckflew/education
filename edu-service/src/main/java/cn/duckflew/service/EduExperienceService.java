package cn.duckflew.service;

import cn.duckflew.entity.professor.EduExperience;
import cn.duckflew.mapper.EduExperienceMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Service
public class EduExperienceService extends ServiceImpl<EduExperienceMapper, EduExperience>
{
    @Autowired
    EduExperienceMapper eduExperienceMapper;
}
