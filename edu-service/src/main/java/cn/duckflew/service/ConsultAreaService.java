package cn.duckflew.service;

import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.ProfessorAnswerArea;
import cn.duckflew.entity.StudyGuideConsultArea;
import cn.duckflew.entity.UserConsultArea;
import cn.duckflew.exception.AreaNameExistException;
import cn.duckflew.exception.ConsultAreaIdInvalidException;
import cn.duckflew.mapper.*;
import cn.duckflew.vo.ConsultAreaVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Service
@Slf4j
public class ConsultAreaService extends ServiceImpl<ConsultAreaMapper, ConsultArea>
{
    @Autowired
    ConsultAreaMapper consultAreaMapper;
    /**
     * 检查导图名是否重复
     * @param areaName 领域名
     * @param isUpdate 是否更新操作,是更新操作就校验和原来的名字是否一致以及新名字是否和库里面冲突
     * @param checkId 不是更新操作不用到这个值,如果是更新操作就和新名字查出来的结果比对
     */
    public void checkAreaName(String areaName,boolean isUpdate,String checkId)
    {
        ConsultArea exist=consultAreaMapper.selectOne(
                new QueryWrapper<ConsultArea>()
                        .eq("area_name",areaName)
                        .eq("parent_id","0")
        );
        if (exist==null)return ;
        if (isUpdate)
        {
            if (checkId==null||checkId.isEmpty())throw new AreaNameExistException("checkId不能为空",areaName);
            if (!exist.getId().equals(checkId))throw new AreaNameExistException("新导图名已存在,请尝试别的名字",areaName);
            return ;
        }
        throw new AreaNameExistException("领域名已经存在",areaName);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateArea(String id, String consultAreaName)
    {
        checkAreaName(consultAreaName,true,id);
        ConsultArea ca = consultAreaMapper.selectById(id);
        if (ca==null)throw new ConsultAreaIdInvalidException("学习导图id无效",id);
        ca.setAreaName(consultAreaName);
        consultAreaMapper.updateById(ca);
    }
    public String makeId(String parentId)
    {
        if (parentId.equals("0"))
        {
            StringBuffer maxIdBuffer=new StringBuffer();
             consultAreaMapper.selectList(new QueryWrapper<ConsultArea>().eq("parent_id", "0"))
                     .stream().max(Comparator.comparingInt(area -> Integer.parseInt(area.getId()))).ifPresent(
                             maxIdTopArea-> maxIdBuffer.append(maxIdTopArea.getId())
                     );
            log.info("生成顶级节点id,当前max顶级节点Id:{}",maxIdBuffer.toString());
            return String.valueOf(Integer.parseInt(maxIdBuffer.toString())+1);
        }
        String maxChildId = getMaxChildId(parentId);
        if (maxChildId==null)return parentId+".1";
        return parentId+"."+(Integer.parseInt(maxChildId.substring(parentId.length() + 1))+1);
    }

    private String getMaxChildId(String parentId)
    {
        List<ConsultArea> allArea = consultAreaMapper.selectList(null);
        List<ConsultArea> consultAreaList=allArea.stream().filter(area->area.getParentId().equals(parentId)&&!area.getId().equals(parentId))
                .sorted((area1, area2) -> {
                    int id1 = Integer.parseInt(area1.getId().substring(parentId.length() + 1));
                    int id2 = Integer.parseInt(area2.getId().substring(parentId.length() + 1));
                    return id2-id1;
                }).collect(Collectors.toList());
        if (consultAreaList.isEmpty())return null;
        return consultAreaList.get(0).getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addArea(ConsultArea consultArea)
    {
        checkAreaName(consultArea.getAreaName(),false, consultArea.getParentId());
        String newId = makeId(consultArea.getParentId());
        consultArea.setId(newId);
        log.info("生成consultArea id:{}",newId);
        consultAreaMapper.insert(consultArea);
    }

    public List<ConsultAreaVo> allVo()
    {
        List<ConsultArea> all = consultAreaMapper.selectList(null);
        return areaListToVo(all);
    }

    /**
     * 此方法用于把一些零散的area节点建立成树
     * @param consultAreaList
     * @return
     */
    public List<ConsultAreaVo> areaListToVo(List<ConsultArea> consultAreaList)
    {
        List<ConsultAreaVo> res = consultAreaList.stream().map(ConsultAreaVo::new).collect(Collectors.toList());
        Map<String, List<ConsultAreaVo>> group = res.stream().collect(Collectors.groupingBy(ConsultAreaVo::getParentId));
        res.forEach(caVo->{
            caVo.setChildrenAreas(group.get(caVo.getId()));
        });
        return  res.stream().filter(
                ca->ca.getParentId().equals("0")
        ).collect(Collectors.toList());
    }

    /**
     * 此方法用于获取数据中的数据子树
     * @param root
     * @return
     */
    public ConsultAreaVo areaToVo(ConsultArea root)
    {
        ConsultAreaVo rootVo = new ConsultAreaVo(root);
        Queue<ConsultAreaVo> queue=new ArrayDeque<>();
        queue.add(rootVo);
        while (!queue.isEmpty())
        {
            ConsultAreaVo curVo = queue.poll();
            List<ConsultAreaVo> childrenList =
                    consultAreaMapper.selectList(new QueryWrapper<ConsultArea>()
                            .eq("parent_id", curVo.getId())).stream().map(ConsultAreaVo::new)
                            .collect(Collectors.toList());
            curVo.setChildrenAreas(childrenList);
            queue.addAll(childrenList);
        }
        return rootVo;
    }

    @Autowired
    ProfessorAnswerAreaMapper professorAnswerAreaMapper;
    @Autowired
    UserConsultAreaMapper userConsultAreaMapper;
    @Autowired
    StudyGuideMapper studyGuideMapper;
    @Autowired
    StudyGuideConsultAreaMapper studyGuideConsultAreaMapper;
    @Transactional(rollbackFor = Exception.class)
    public void  deleteById(String areaId)
    {
        consultAreaMapper.deleteByRoot(areaId);
        /**
         * 根据areaId 删除所有相关数据
         */
        userConsultAreaMapper.delete(
                new QueryWrapper<UserConsultArea>()
                .like("area_id",areaId+"%")
        );
        professorAnswerAreaMapper.delete(
                new QueryWrapper<ProfessorAnswerArea>()
                        .like("consult_area_id",areaId+"%")
        );
        studyGuideConsultAreaMapper.delete(
                new QueryWrapper<StudyGuideConsultArea>()
                        .like("consult_area_id",areaId+"%")
        );
    }
    public List<ConsultAreaVo> getChildrenAndSelfVoByParentId(String parentId)
    {
        return areaListToVo(consultAreaMapper.selectChildListByParentId(parentId));
    }
    public List<ConsultArea> getChildrenAndSelfByParentId(String parentId)
    {
        return consultAreaMapper.selectChildListByParentId(parentId);
    }

    /**
     * 获取用户感兴趣的列表
     * @param userId
     * @return
     */
    public List<ConsultArea> userLikeAreaList(Integer userId)
    {
        List<UserConsultArea> userConsultAreaList = userConsultAreaMapper.selectList(new QueryWrapper<UserConsultArea>().eq("user_id", userId));
        if (userConsultAreaList.isEmpty())return new ArrayList<>();
        return consultAreaMapper.selectBatchIds(
                userConsultAreaList.stream().map(UserConsultArea::getAreaId).collect(Collectors.toList())
        );
    }


    @Autowired
    ProfessorAnswerAreaService professorAnswerAreaService;


    public Set<String> getChildIdSet(List<String> consultAreaIdList)
    {
        Set<String> res=new HashSet<>();
        for (String parentId : consultAreaIdList)
        {
            List<String> childrenIdList = consultAreaMapper.selectChildListByParentId(parentId).stream().map(ConsultArea::getId).collect(Collectors.toList());
            res.addAll(childrenIdList);
        }
        return res;
    }
}
