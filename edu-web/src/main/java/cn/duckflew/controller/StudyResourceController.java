package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.annotation.ProfessorAccess;
import cn.duckflew.entity.professor.StudyResource;
import cn.duckflew.service.StudyResourceService;
import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.vo.PageRes;
import cn.duckflew.vo.StudyResourceVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学习资源相关接口
 */
@RestController
@RequestMapping("/studyResource")
public class StudyResourceController
{
    @Autowired
    StudyResourceService studyResourceService;

    /**
     * 发布学习资源
     * @param studyResource
     * @return
     * @apiNote 发布学习资源和上传头像等等是一样的步骤,先上传文件，再写入FileId
     */
    @ProfessorAccess
    @PostMapping("/")
    public SaResult addStudyResource(@RequestBody @Validated(AddGroup.class) StudyResource studyResource)
    {
        studyResource.setProfessorId(StpUtil.getLoginIdAsInt());
        studyResource.setCreateTime(new Date());
        studyResourceService.add(studyResource);
        return SaResult.ok().setMsg("添加成功");
    }

    /**
     * 搜索学习资源
     * @param pageNum
     * @param pageSize
     * @param keyword 关键词
     * @return
     * @apiNote 关键词可以为空,资源首页默认就展示一定量的学习资料，也是调用这个接口，只是不传入keyword就可以
     */
    @GetMapping("/search")
    public SaResult searchStudyResource(
            @RequestParam(required = false,defaultValue = "0")
            Integer pageNum,
            @RequestParam(required = false,defaultValue = "5")
            Integer pageSize,
            @RequestParam(required = false)
            String keyword
    )
    {
        Page<StudyResource> page = new Page<>(pageNum, pageSize);
        QueryWrapper<StudyResource> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword))
        {
            qw.like("resource_name","%"+keyword+"%");
        }
        studyResourceService.page(page,qw);
        List<StudyResourceVo> voList = page.getRecords().stream().map(studyResourceService::studyResourceVo).collect(Collectors.toList());
        PageRes<StudyResourceVo> res = new PageRes<>();
        res.setList(voList);
        res.setTotal(page.getTotal());
        return SaResult.ok().setData(res);
    }


    /**
     * 获取自己上传的所有资源
     * @param pageNum
     * @param pageSize
     * @return
     * @apiNote  自己上传的资源在个人主页开设一个版块来提供查看
     */
    @GetMapping("/own")
    @SaCheckLogin
    @ProfessorAccess
    public SaResult getOwnStudyResource(
            @RequestParam(required = false,defaultValue = "0")
                    Integer pageNum,
            @RequestParam(required = false,defaultValue = "5")
                    Integer pageSize
    )
    {
        int professorId = StpUtil.getLoginIdAsInt();
        Page<StudyResource> page = new Page<>(pageNum,pageSize);
        studyResourceService.pageByProfessorId(page,professorId );

        List<StudyResourceVo> voList = page.getRecords().stream().map(studyResourceService::studyResourceVo).collect(Collectors.toList());
        PageRes<StudyResourceVo> res = new PageRes<>();
        res.setList(voList);
        res.setTotal(page.getTotal());
        return SaResult.ok().setData(res);
    }
}
