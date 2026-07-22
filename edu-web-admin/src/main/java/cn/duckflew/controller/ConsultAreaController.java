package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.ConsultArea;
import cn.duckflew.service.ConsultAreaService;
import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import cn.duckflew.vo.ConsultAreaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理: 咨询领域相关接口
 */
@RequestMapping("/consultArea")
@RestController
public class ConsultAreaController
{

    @Autowired
    ConsultAreaService consultAreaService;

    /**
     * 获取所有咨询领域
     * @return
     */
    @GetMapping("/")
    @SaCheckPermission("study_guide")
    public SaResult getAllArea()
    {
        List<ConsultAreaVo> all = consultAreaService.allVo();
        return SaResult.ok().setData(all);
    }

    /**
     * 添加咨询领域
     * @param consultArea
     * @return
     */
    @PostMapping("/")
    @SaCheckPermission("consult_area")
    public SaResult addArea(@RequestBody @Validated(AddGroup.class) ConsultArea consultArea)
    {
        consultAreaService.addArea(consultArea);
        return SaResult.ok().setMsg("添加咨询领域成功");
    }

    /**
     * 修改领域名称
     * @param consultArea
     * @return
     */
    @PutMapping("/")
    @SaCheckPermission("consult_area")
    public SaResult updateArea(@RequestBody @Validated(UpdateGroup.class) ConsultArea consultArea)
    {
        consultAreaService.updateArea(consultArea.getId(),consultArea.getAreaName());
        return SaResult.ok().setMsg("修改成功");
    }

    /**
     * 删除领域
     * @param areaId id
     * @return
     * @apiNote 注意，这个接口不止会删除单个节点，而且会删除旗下的所有子节点，并且会删除其他的功能中所有有关的数据
     */
    @DeleteMapping("/{areaId}")
    @SaCheckPermission("consult_area")
    public SaResult deleteArea(
            @PathVariable String areaId
    )
    {
        consultAreaService.deleteById(areaId);
        return SaResult.ok().setMsg("删除成功");
    }


}
