package cn.duckflew.mapper;

import cn.duckflew.entity.StudyGuide;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface StudyGuideMapper extends BaseMapper<StudyGuide>
{
    void deleteStudyGuide(String rootId);

    String getMaxChildId(String parentId);

}
