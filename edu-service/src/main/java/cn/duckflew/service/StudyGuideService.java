package cn.duckflew.service;

import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.StudyGuide;
import cn.duckflew.entity.StudyGuideConsultArea;
import cn.duckflew.entity.UserConsultArea;
import cn.duckflew.exception.GuideNameExistException;
import cn.duckflew.exception.StudyGuideIdInvalidException;
import cn.duckflew.mapper.ConsultAreaMapper;
import cn.duckflew.mapper.StudyGuideConsultAreaMapper;
import cn.duckflew.mapper.StudyGuideMapper;
import cn.duckflew.mapper.UserConsultAreaMapper;
import cn.duckflew.vo.RecommendStudyGuidePage;
import cn.duckflew.vo.StudyGuideVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudyGuideService extends ServiceImpl<StudyGuideMapper, StudyGuide>
{

    @Autowired
    StudyGuideMapper studyGuideMapper;

    @Autowired
    StudyGuideConsultAreaMapper studyGuideConsultAreaMapper;
    @Autowired
    ConsultAreaMapper consultAreaMapper;

    @Autowired
    ConsultAreaService consultAreaService;

    /**
     * 检查导图名是否重复
     * @param guideName 导图名
     * @param isUpdate 是否更新操作,是更新操作就校验和原来的名字是否一致以及新名字是否和库里面冲突
     * @param checkId 不是更新操作不用到这个值,如果是更新操作就和新名字查出来的结果比对
     */
    public void checkGuideName(String guideName,boolean isUpdate,String checkId)
    {
        StudyGuide exist=studyGuideMapper.selectOne(
                new QueryWrapper<StudyGuide>()
                        .eq("node_name",guideName)
                        .eq("parent_id",0)
        );
        if (exist==null)return ;
        if (isUpdate)
        {
            if (checkId==null||checkId.isEmpty())throw new GuideNameExistException("checkId不能为空",guideName);
            if (!exist.getId().equals(checkId))throw new GuideNameExistException("新导图名已存在,请尝试别的名字",guideName);
            return ;
        }
        if (exist!=null) throw new GuideNameExistException("此导图名已经存在",guideName);
    }

    /**
     * 检查节点名是否重复
     * @param nodeName 节点名
     * @param parentId 父亲id
     */
    public void checkGuideNodeName(String nodeName,String parentId)
    {
        StudyGuide exist=studyGuideMapper.selectOne(
                new QueryWrapper<StudyGuide>()
                        .eq("node_name",nodeName)
                        .eq("parent_id",parentId)
        );
        if (exist!=null) throw new GuideNameExistException("此节点名已经存在",nodeName);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateStudyGuide(String studyGuideId,List<String> consultAreaIdList, String studyGuideName)
    {
        checkGuideName(studyGuideName,true,studyGuideId);
        StudyGuide sg = studyGuideMapper.selectById(studyGuideId);
        if (sg==null)throw new StudyGuideIdInvalidException("学习导图id无效",studyGuideId);
        sg.setNodeName(studyGuideName);
        studyGuideMapper.updateById(sg);
        deleteStudyGuideArea(studyGuideId);
        addGuideArea(studyGuideId,consultAreaIdList);
    }

    /**
     * 给学习导图添加咨询领域
     * @param guideId 导图id
     * @param consultAreaIdList 领域id数组
     */
    public void addGuideArea(String guideId,List<String> consultAreaIdList)
    {
        log.info("添加新的导图领域,areaListSize:{}",consultAreaIdList.size());
        Set<ConsultArea> consultAreaSet=new HashSet<>();
        consultAreaIdList.forEach(areaId->{
            consultAreaSet.addAll(
                    consultAreaMapper.selectList(
                            new QueryWrapper<ConsultArea>()
                            .likeRight("id",areaId)
                    )
            );
        });
        log.info("需要添加的consultAreaSet:{}",consultAreaSet);
        for (ConsultArea consultArea : consultAreaSet)
        {
            StudyGuideConsultArea sgConsultArea = new StudyGuideConsultArea();
            sgConsultArea.setConsultAreaId(consultArea.getId());
            sgConsultArea.setGuideRootId(guideId);
            studyGuideConsultAreaMapper.insert(sgConsultArea);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void addStudyGuide(List<String> consultAreaIdList, String studyGuideName)
    {
        checkGuideName(studyGuideName,false,"");
        StudyGuide sg = new StudyGuide();
        sg.setNodeName(studyGuideName);
        sg.setParentId("0");
        sg.setIsImportant(0);
        sg.setId(makeId("0"));
        studyGuideMapper.insert(sg);
        addGuideArea(sg.getId(),consultAreaIdList);
    }

    public void deleteStudyGuideArea(String guideId)
    {
        int deleteRows = studyGuideConsultAreaMapper.delete(new QueryWrapper<StudyGuideConsultArea>().eq("guide_root_id", guideId));
        log.info("删除导图原来关联的领域{},删除{}行记录",guideId,deleteRows);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteStudyGuide(String rootId)
    {
        StudyGuide node = studyGuideMapper.selectById(rootId);
        if (node.getParentId().equals("0"))
        {
            log.info("删除根节点:{}",rootId);
            studyGuideConsultAreaMapper.delete(
                    new QueryWrapper<StudyGuideConsultArea>()
                    .eq("guide_root_id",rootId)
            );
        }
        studyGuideMapper.deleteStudyGuide(rootId);
    }

    public String makeId(String parentId)
    {
        if (parentId.equals("0"))
        {
            String maxId = studyGuideMapper.selectList(new QueryWrapper<StudyGuide>().eq("parent_id", 0).orderByDesc("id")).get(0).getId();
            log.info("生成顶级节点id,当前mxaId:{}",maxId);
            return String.valueOf(Integer.parseInt(maxId)+1);
        }
        String maxChildId = studyGuideMapper.getMaxChildId(parentId);
        if (maxChildId==null)return parentId+".1";
        return parentId+"."+(Integer.parseInt(maxChildId.substring(parentId.length() + 1))+1);
    }
    @Transactional(rollbackFor = Exception.class)
    public void addStudyGuideNode(String parentId, String nodeName, Integer isImportant)
    {
        checkGuideNodeName(nodeName,parentId);
        StudyGuide sg = new StudyGuide();
        sg.setParentId(parentId);
        String newId = makeId(parentId);
        sg.setNodeName(nodeName);
        sg.setIsImportant(isImportant);
        log.info("生成studyGuide id{}",newId);
        sg.setId(newId);
        studyGuideMapper.insert(sg);
    }
    public StudyGuideVo guideToVo(StudyGuide root)
    {
        StudyGuideVo rootVo = new StudyGuideVo(root);
        Queue<StudyGuideVo> queue=new ArrayDeque<>();
        queue.add(rootVo);
        while (!queue.isEmpty())
        {
            StudyGuideVo curVo = queue.poll();
            List<StudyGuideVo> childrenList =
                    studyGuideMapper.selectList(new QueryWrapper<StudyGuide>()
                            .eq("parent_id", curVo.getId())).stream().map(StudyGuideVo::new)
                            .collect(Collectors.toList());
            curVo.setChildren(childrenList);
            queue.addAll(childrenList);
        }
        return rootVo;
    }

    @Autowired
    UserConsultAreaMapper userConsultAreaMapper;
    public RecommendStudyGuidePage recommendStudyGuideByArea(Integer userId, Integer pageNum, Integer pageSize)
    {
        RecommendStudyGuidePage res = new RecommendStudyGuidePage();
        /**
         * 查询出用户感兴趣的areaIds
         */
        List<String> areaIdList = userConsultAreaMapper.selectList(new QueryWrapper<UserConsultArea>()
        .eq("user_id",userId)).stream().map(UserConsultArea::getAreaId).collect(Collectors.toList());
        if (areaIdList.isEmpty())
        {
            log.info("用户暂未设置偏好,进行全范围推荐学习导图");
            areaIdList=consultAreaMapper.selectList(null).stream().map(ConsultArea::getId).collect(Collectors.toList());
        }
        Set<String> studyGuideIdSet=studyGuideConsultAreaMapper.selectList(
                new QueryWrapper<StudyGuideConsultArea>()
                .in("consult_area_id",areaIdList)
        ).stream().map(StudyGuideConsultArea::getGuideRootId).collect(Collectors.toSet());

        log.info("studyGuideIdSet:{}",studyGuideIdSet);
        if (studyGuideIdSet.isEmpty())
        {
            res.setTotal(0);
            res.setStudyGuides(null);
            return res;
        }
        Page<StudyGuide> page = new Page<>(pageNum, pageSize);
        studyGuideMapper.selectPage(
                page,
                new QueryWrapper<StudyGuide>()
                    .in("id",studyGuideIdSet)
        );
        res.setStudyGuides(page.getRecords());
        res.setTotal(Integer.parseInt(String.valueOf(page.getTotal())));
        return res;
    }

    public List<ConsultArea> listGuideArea(String guideId)
    {
        StudyGuide exist = studyGuideMapper.selectById(guideId);
        if (exist==null)throw new StudyGuideIdInvalidException("导图id无效",guideId );
        List<String> consultAreaIds= studyGuideConsultAreaMapper.selectList(
                new QueryWrapper<StudyGuideConsultArea>()
                .eq("guide_root_id",guideId)
        ).stream().map(StudyGuideConsultArea::getConsultAreaId).collect(Collectors.toList());
        if (consultAreaIds.isEmpty())return null;
        return consultAreaMapper.selectBatchIds(consultAreaIds);
    }
}
