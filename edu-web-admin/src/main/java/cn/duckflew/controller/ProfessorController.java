package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.ProInfo;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.enums.UserType;
import cn.duckflew.service.BaseUserService;
import cn.duckflew.service.ProfessorService;
import cn.duckflew.vo.PageRes;
import cn.duckflew.vo.ProfessorInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台管理:教授相关接口
 */
@RestController
@RequestMapping("/professor")
public class  ProfessorController
{


    @Autowired
    BaseUserService baseUserService;

    /**
     * 分页获取所有待审核教授
     * @param pageNum 分页参数 可以为空
     * @param pageSize 分页参数 可以为空
     * @param professorType 教授类型  2为待审核教授,3为正式教授
     * @since 不可以为空
     * @param keyword 搜索关键字 主要根据真名搜索
     * @since 可以为空
     * @response
     * {
     *     "list":[],
     *     "total": 0
     * }
     */
    @GetMapping("/")
    @SaCheckLogin
    @SaCheckPermission("pro")
    public SaResult getAllPreProfessor(
            @RequestParam(defaultValue = "0",required = false)
            Integer pageNum,
            @RequestParam(defaultValue = "5",required = false)
            Integer pageSize,
            @NotNull(message = "教授类型不能为空")
            @Min(value = 2,message = "只能为2或3")
            @Max(value = 3,message = "只能为2或3")
            Integer professorType,
            String keyword
    )
    {
        Page<BaseUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<BaseUser> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword))wrapper.like("real_name",keyword);
        wrapper.eq("user_type",professorType);
        baseUserService.page(page,wrapper);
        PageRes<ProfessorInfo> res = new PageRes<>();
        res.setTotal(page.getTotal());
        res.setList(page.getRecords().stream().map(pro->professorService.professorInfo(pro)).collect(Collectors.toList()));
        return SaResult.ok().setData(res).setMsg("获取成功");
    }


    @Autowired
    ProfessorService professorService;
    /**
     * 通过教授审核
     * @param userId 用户Id
     * @return SaResult
     * @response {
     *     "code": 200,
     *     "msg": "教授申请审核通过",
     *     "data": null
     * }
     */
    @PostMapping("/access/{userId}")
    public SaResult accessProfessorProfile(@PathVariable Integer userId)
    {
        professorService.accessProfessorReq(userId);
        return SaResult.ok().setMsg("教授申请审核通过");
    }

    /**
     * 添加教授简介(已实现)
     * @return
     */
    @PostMapping("/introduction/{proId}")
    public SaResult addIntroduction(@PathVariable  Integer proId,String introContent)
    {
        professorService.addIntroduction(proId,introContent);
        return SaResult.ok().setMsg("操作成功");
    }
    /**
     * 设置教授职称(已实现)
     * @return
     */
    @PutMapping("/jobRank/{proId}")
    public SaResult setJobRankOfProfessor(
            @PathVariable Integer proId,
          @NotNull(message = "职称id不能为空")
          Integer jobRankId)
    {
        professorService.setJobRankId(proId,jobRankId);
        return SaResult.ok().setMsg("操作成功");
    }

    /**
     * 删除教授资格
     * @return
     */
    @DeleteMapping("/cancel/{proId}")
    @SaCheckPermission("pro")
    @SaCheckLogin
    public SaResult cancelProfessorRight(
            @PathVariable Integer proId
    )
    {
        professorService.cancelPro(proId);
        return SaResult.ok();
    }






}
