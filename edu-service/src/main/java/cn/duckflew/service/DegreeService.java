package cn.duckflew.service;

import cn.duckflew.entity.professor.Degree;
import cn.duckflew.mapper.DegreeMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DegreeService extends ServiceImpl<DegreeMapper, Degree>
{
}
