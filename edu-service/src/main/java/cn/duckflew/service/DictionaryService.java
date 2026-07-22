package cn.duckflew.service;

import cn.duckflew.entity.Dictionary;
import cn.duckflew.mapper.DictionaryMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service(value = "dictionaryService")
public class DictionaryService extends ServiceImpl<DictionaryMapper, Dictionary>
{
}
