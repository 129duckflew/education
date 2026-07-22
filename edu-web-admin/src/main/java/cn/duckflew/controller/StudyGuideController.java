package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.StudyGuide;
import cn.duckflew.service.ConsultAreaService;
import cn.duckflew.service.StudyGuideService;
import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import cn.duckflew.vo.AddStudyGuideNodeParam;
import cn.duckflew.vo.StudyGuideParam;
import cn.duckflew.vo.StudyGuideVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理:学习导图相关接口
 */
@RequestMapping("/studyGuide")
@RestController
public class StudyGuideController
{

    @Autowired
    StudyGuideService studyGuideService;

    /**
     * 根据id查询学习导图详情
     * @param guideId 学习导图id
     * @return
     */
    @GetMapping("/{guideId}")
    @SaCheckPermission("study_guide")
    @SaCheckLogin
    public SaResult pageGuide(
            @PathVariable Integer guideId
    )
    {
        StudyGuide guide = studyGuideService.getById(guideId);
        StudyGuideVo res = studyGuideService.guideToVo(guide);
        return SaResult.ok().setData(res);
    }
    /**
     * 分页查询学习导图
     * @param pageNum
     * @param pageSize
     * @apiNote 2个参数可以为空 默认05
     * @return
     */
    @GetMapping("/")
    @SaCheckPermission("study_guide")
    @SaCheckLogin
    public SaResult pageGuide(
            @RequestParam(required = false,defaultValue = "0") Integer pageNum,
            @RequestParam(required = false,defaultValue = "5") Integer pageSize
    )
    {
        Page<StudyGuide> page = new Page<>(pageNum, pageSize);
        studyGuideService.page(page,
                new QueryWrapper<StudyGuide>()
        .eq("parent_id",0));
        return SaResult.ok().setData(page);
    }
    /**
     * 修改学习导图
     * @param updateStudyGuideParam 参数
     * @return
     */
    @PutMapping("/")
    public SaResult updateStudyGuide(@Validated({UpdateGroup.class}) @RequestBody StudyGuideParam updateStudyGuideParam)
    {
        studyGuideService.updateStudyGuide(updateStudyGuideParam.getStudyGuideId(),updateStudyGuideParam.getConsultAreaIdList(),updateStudyGuideParam.getStudyGuideName());
        return SaResult.ok().setMsg("修改学习导图成功");
    }
    /**
     * 添加学习导图
     * @param addStudyGuideParam 参数
     * @return
     * @apiNote 提交相关领域的id还是和以前一样，1->{2,3,4} 如果全选234 那么只需要提交1即可
     */
    @PostMapping("/")
    public SaResult addStudyGuide(@Validated({AddGroup.class}) @RequestBody StudyGuideParam addStudyGuideParam)
    {
        studyGuideService.addStudyGuide(addStudyGuideParam.getConsultAreaIdList(),addStudyGuideParam.getStudyGuideName());
        return SaResult.ok().setMsg("添加学习导图成功");
    }
    /**
     * 添加导图节点
     * @param addStudyGuideNodeParam 参数
     * @return
     * @apiNote 提交相关领域的id还是和以前一样，1->{2,3,4} 如果全选234 那么只需要提交1即可
     */
    @PostMapping("/node")
    public SaResult addStudyGuideNode(@Validated @RequestBody AddStudyGuideNodeParam addStudyGuideNodeParam )
    {
        studyGuideService.addStudyGuideNode(
                addStudyGuideNodeParam.getParentId()
                ,addStudyGuideNodeParam.getNodeName()
                ,addStudyGuideNodeParam.getIsImportant());
        return SaResult.ok().setMsg("添加学习导图成功");
    }

    /**
     * 删除学习导图节点
     * @param rootId 导图id
     * @return
     * @apiNote 如果删除根节点,那么就是删除整张学习导图
     */
    @DeleteMapping("/{rootId}")
    public SaResult deleteGuide(
            @NotNull(message = "学习导图id不能为空")
            @PathVariable String rootId
    )
    {
        studyGuideService.deleteStudyGuide(rootId);
        return SaResult.ok();
    }

    /**
     * 根据导图id获取关联的领域
     * @param guideId
     * @return
     */
    @GetMapping("/area/")
    @SaCheckPermission("study_guide")
    public SaResult getGuideAreaList(
            @NotBlank(message = "导图id不能为空") String guideId
    )
    {
        List<ConsultArea> consultAreaList= studyGuideService.listGuideArea(guideId);
        return SaResult.ok().setData(consultAreaList);
    }
}
