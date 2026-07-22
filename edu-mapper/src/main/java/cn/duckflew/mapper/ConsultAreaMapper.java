package cn.duckflew.mapper;

import cn.duckflew.entity.ConsultArea;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
public interface ConsultAreaMapper extends BaseMapper<ConsultArea>
{

    List<ConsultArea> selectChildListByParentId(String parentId);

    void deleteByRoot(String areaId);
}
