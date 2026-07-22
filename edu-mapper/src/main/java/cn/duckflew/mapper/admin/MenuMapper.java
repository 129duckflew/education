package cn.duckflew.mapper.admin;

import cn.duckflew.entity.admin.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
public interface MenuMapper extends BaseMapper<Menu>
{
    List<Menu> getBottomNodeList();

}
