package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.entity.professor.EduExperience;
import cn.duckflew.entity.professor.EduDataSource;
import cn.duckflew.service.BaseUserService;
import cn.duckflew.service.EduDataSourceService;
import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.vo.EduDataSourceVo;
import cn.duckflew.vo.PageRes;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理:学信网数据相关接口
 */
@SuppressWarnings("AlibabaClassMustHaveAuthor")
@RestController
@RequestMapping("edu_datasource")
public class EduDataSourceController
{

    @Autowired
    BaseUserService baseUserService;
    @Autowired
    EduDataSourceService eduDataSourceService;


    /**
     *  通过用户id查询学信网数据
     * @param userId
     * @return
     */
    @GetMapping("/user/{userId}")
    @SaCheckPermission("pro")
    public SaResult getUserEduDataSource(@PathVariable Integer userId)
    {
        BaseUser baseUser = baseUserService.getById(userId);
        EduDataSource eduDataSource = eduDataSourceService.getOne(
                new QueryWrapper<EduDataSource>().eq("card_id",baseUser.getCardId())
        );
        if (eduDataSource==null)
            return SaResult.ok().setMsg("暂无此用户的学信网数据");
        EduDataSourceVo res = eduDataSourceService.eduDataSourceVo(eduDataSource);
        return SaResult.ok().setMsg("获取学信网数据成功").setData(res);
    }

    /**
     *  通过身份证查询学信网数据
     * @param cardId 身份证号
     * @return
     */
    @GetMapping("/by_cardId")
    @SaCheckPermission("pro")
    public SaResult getEduDataSource( String cardId)
    {
        EduDataSource eduDataSource = eduDataSourceService.getOne(
                new QueryWrapper<EduDataSource>().eq("card_id",cardId)
        );
        if (eduDataSource==null)
            return SaResult.ok().setMsg("暂无此用户的学信网数据");
        EduDataSourceVo res = eduDataSourceService.eduDataSourceVo(eduDataSource);
        return SaResult.ok().setMsg("获取学信网数据成功").setData(res);
    }


    /**
     * 添加数据源
     * @param eduDataSource
     * @return
     */
    @PostMapping("/")
    public SaResult addDataSource(@Validated({AddGroup.class})@RequestBody  EduDataSourceVo eduDataSource)
    {
        Integer newId = eduDataSourceService.add(eduDataSource);
        return SaResult.ok().setMsg("添加成功").setData(newId);
    }

    /**
     * 为某个人的数据源添加教育经历
     * @param eduExperience
     * @return
     */
    @PostMapping("/experience/")
    public SaResult addExperienceToDatasource(@RequestBody @Validated EduExperience eduExperience)
    {
        eduDataSourceService.addExperienceToDatasource(eduExperience);
        return SaResult.ok().setMsg("添加成功");
    }

    /**
     * 删除数据源
     * @param datasourceId
     * @return
     */
    @DeleteMapping("/{datasourceId}")
    public SaResult deleteById(@PathVariable Integer datasourceId)
    {
        eduDataSourceService.deleteById(datasourceId);
        return SaResult.ok().setMsg("删除成功");
    }

    /**
     * 分页获取学信网数据源
     * @param pageSize 分页大小
     * @since 默认值5
     * @param pageNum 分页数
     * @since 默认值0
     * @return
     */
    @GetMapping("/")
    public SaResult pageDatasource(
            @RequestParam(defaultValue = "5",required = false)
            Integer pageSize,
            @RequestParam(defaultValue = "0",required = false)
            Integer pageNum
    )
    {
        Page<EduDataSource> page = new Page<>(pageNum, pageSize);
        eduDataSourceService.page(page);
        List<EduDataSourceVo> list = page.getRecords().stream().map(eduDataSourceService::eduDataSourceVo).collect(Collectors.toList());
        PageRes<EduDataSourceVo> res = new PageRes<>();
        res.setList(list);
        res.setTotal(page.getTotal());
        return SaResult.ok().setData(res);
    }

}
