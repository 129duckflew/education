package cn.duckflew.mapper;

import cn.duckflew.entity.Dictionary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

@Component(value = "dictionaryMapper")
public interface DictionaryMapper extends BaseMapper<Dictionary>
{
}
