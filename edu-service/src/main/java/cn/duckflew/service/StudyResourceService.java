package cn.duckflew.service;

import cn.duckflew.entity.FileRecord;
import cn.duckflew.entity.professor.StudyResource;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.exception.FileIdInvalidException;
import cn.duckflew.mapper.BaseUserMapper;
import cn.duckflew.mapper.FileRecordMapper;
import cn.duckflew.mapper.StudyResourceMapper;
import cn.duckflew.vo.StudyResourceVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyResourceService extends ServiceImpl<StudyResourceMapper,StudyResource>
{
    @Autowired
    StudyResourceMapper studyResourceMapper;
    public void add(StudyResource studyResource)
    {
        studyResourceMapper.insert(studyResource);
    }

    public void pageByProfessorId(Page<StudyResource> page, int professorId)
    {
        studyResourceMapper.selectPage(
                page,new QueryWrapper<StudyResource>()
                .eq("professor_id",professorId)
        );
    }

    @Autowired
    FileRecordMapper fileRecordMapper;
    @Autowired
    BaseUserMapper baseUserMapper;
    public StudyResourceVo studyResourceVo(StudyResource studyResource)
    {
        if (studyResource==null)return null;
        StudyResourceVo res = new StudyResourceVo();
        BeanUtils.copyProperties(studyResource,res);
        if (studyResource.getFileId()!=null)
        {
            FileRecord fileRecord = fileRecordMapper.selectById(studyResource.getFileId());
            if (fileRecord==null)
                throw new FileIdInvalidException("学习资源文件id无效",studyResource.getFileId());
            res.setFileRecord(fileRecord);
        }
        BaseUser professor = baseUserMapper.selectById(studyResource.getProfessorId());
        if (professor!=null)
        {
            res.setUserNickname(professor.getNickName());
            res.setUserAvatar(professor.getAvatar());
        }
        return res;
    }
}
