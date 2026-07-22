package cn.duckflew.service;

import cn.duckflew.entity.ProInfo;
import cn.duckflew.entity.professor.JobRank;
import cn.duckflew.mapper.JobRankMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobRankService extends ServiceImpl<JobRankMapper, JobRank>
{
}
