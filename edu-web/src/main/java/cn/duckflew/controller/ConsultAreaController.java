package cn.duckflew.controller;

import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.ConsultArea;
import cn.duckflew.service.ConsultAreaService;
import cn.duckflew.vo.AddConsultAreaParams;
import cn.duckflew.vo.ConsultAreaVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * 咨询领域相关接口
 */
@SuppressWarnings("AlibabaClassMustHaveAuthor")
@RestController
@Validated
@RequestMapping("/consult_area")
public class ConsultAreaController
{

    @Autowired
    ConsultAreaService consultAreaService;

    /**
     * 获取所有顶层咨询领域
     * @return
     * @response {
     * code: 200,
     * msg: "ok",
     * data: [
     * {
     * id: 1,
     * areaName: "小学",
     * parentId: 0
     * },
     * {
     * id: 2,
     * areaName: "初中",
     * parentId: 0
     * },
     * {
     * id: 3,
     * areaName: "高中",
     * parentId: 0
     * },
     * {
     * id: 4,
     * areaName: "大学",
     * parentId: 0
     * },
     * {
     * id: 5,
     * areaName: "出国留学",
     * parentId: 0
     * },
     * {
     * id: 6,
     * areaName: "培训/考证",
     * parentId: 0
     * }
     * ]
     * }
     */
    @GetMapping("/top")
    public SaResult getAllTopAreas()
    {
        List<ConsultArea> topAreas = consultAreaService.list(new QueryWrapper<ConsultArea>().eq("parent_id", 0));
        return SaResult.ok().setData(topAreas);
    }

    /**
     * 获取所有咨询领域(树形结构返回)
     * @return
     * @response {
     * code: 200,
     * msg: "ok",
     * data: [
     * {
     * id: 1,
     * areaName: "小学",
     * parentId: 0,
     * childrenAreas: [
     * {
     * id: 101,
     * areaName: "小学语文",
     * parentId: 1,
     * childrenAreas: null
     * },
     * {
     * id: 102,
     * areaName: "小学数学",
     * parentId: 1,
     * childrenAreas: null
     * },
     * {
     * id: 103,
     * areaName: "小学英语",
     * parentId: 1,
     * childrenAreas: null
     * }
     * ]
     * },
     * {
     * id: 2,
     * areaName: "初中",
     * parentId: 0,
     * childrenAreas: [
     * {
     * id: 201,
     * areaName: "初中语文",
     * parentId: 2,
     * childrenAreas: null
     * },
     * {
     * id: 202,
     * areaName: "初中数学",
     * parentId: 2,
     * childrenAreas: null
     * },
     * {
     * id: 203,
     * areaName: "初中英语",
     * parentId: 2,
     * childrenAreas: null
     * },
     * {
     * id: 204,
     * areaName: "初中物理",
     * parentId: 2,
     * childrenAreas: null
     * },
     * {
     * id: 205,
     * areaName: "初中化学",
     * parentId: 2,
     * childrenAreas: null
     * },
     * {
     * id: 206,
     * areaName: "初中生物",
     * parentId: 2,
     * childrenAreas: null
     * }
     * ]
     * },
     * {
     * id: 3,
     * areaName: "高中",
     * parentId: 0,
     * childrenAreas: null
     * },
     * {
     * id: 4,
     * areaName: "大学",
     * parentId: 0,
     * childrenAreas: [
     * {
     * id: 401,
     * areaName: "工学",
     * parentId: 4,
     * childrenAreas: [
     * {
     * id: 40101,
     * areaName: "计算机科学与技术",
     * parentId: 401,
     * childrenAreas: null
     * },
     * {
     * id: 40102,
     * areaName: "电气工程",
     * parentId: 401,
     * childrenAreas: null
     * },
     * {
     * id: 40103,
     * areaName: "软件工程",
     * parentId: 401,
     * childrenAreas: null
     * }
     * ]
     * },
     * {
     * id: 402,
     * areaName: "农学",
     * parentId: 4,
     * childrenAreas: null
     * },
     * {
     * id: 403,
     * areaName: "医学",
     * parentId: 4,
     * childrenAreas: null
     * },
     * {
     * id: 404,
     * areaName: "理学",
     * parentId: 4,
     * childrenAreas: null
     * }
     * ]
     * },
     * {
     * id: 5,
     * areaName: "出国留学",
     * parentId: 0,
     * childrenAreas: null
     * },
     * {
     * id: 6,
     * areaName: "培训/考证",
     * parentId: 0,
     * childrenAreas: null
     * }
     * ]
     * }
     */
    @GetMapping("/all")
    public SaResult getAll()
    {
        List<ConsultAreaVo> consultAreaList=consultAreaService.allVo();
        return SaResult.ok().setData(consultAreaList);
    }




    /**
     * 根据父节点获取子节点
     * @return
     * @apiNote 无需登录就可以访问
     */
    @GetMapping("/children/{parentId}")
    public SaResult getChildrenByParentId(@PathVariable
                                            @NotNull(message = "父级节点id不能为空")
                                                      String parentId)
    {

        return SaResult.ok().setData(consultAreaService.getChildrenAndSelfVoByParentId(parentId));
    }


    /**
     * 根据领域id 获取领域内容(不包含子节点)
     * @param areaId 咨询领域id
     * @return
     * 
     */
    @GetMapping("/{areaId}")
    public SaResult getAreaFromId(@PathVariable Integer areaId)
    {
        return SaResult.ok().setData(consultAreaService.getById(areaId));
    }
}
