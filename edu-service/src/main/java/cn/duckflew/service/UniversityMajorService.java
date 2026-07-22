package cn.duckflew.service;

import cn.duckflew.entity.UniversityMajor;
import cn.duckflew.mapper.UniversityMajorMapper;
import cn.duckflew.vo.UniversityMajorVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UniversityMajorService extends ServiceImpl<UniversityMajorMapper, UniversityMajor>
{

    @Autowired
    UniversityMajorMapper universityMajorMapper;

    public List<UniversityMajorVo> universityMajorVo(List<UniversityMajor> majorList)
    {
        log.info("majorList to major tree source List={}",majorList);
        List<UniversityMajorVo> res = majorList.stream().map(
                universityMajor -> {
                    UniversityMajorVo vo = new UniversityMajorVo();
                    BeanUtils.copyProperties(universityMajor,vo);
                    return vo;
                }
        ).collect(Collectors.toList());
        Map<Integer, List<UniversityMajorVo>> group = res.stream().collect(Collectors.groupingBy(UniversityMajorVo::getParentId));
        res.forEach(caVo->{
            caVo.setChildren(group.get(caVo.getId()));
        });
        List<UniversityMajorVo> treeRes = res.stream().filter(ca -> ca.getParentId().equals(0)).collect(Collectors.toList());
        if (treeRes.isEmpty())return res;
        return  treeRes;
    }
    public List<UniversityMajorVo> treeOfAll()
    {
        List<UniversityMajor> all = universityMajorMapper.selectList(null);
        return this.universityMajorVo(all);
    }
}
