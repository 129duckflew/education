package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.pay.Order;
import cn.duckflew.service.OrderService;
import cn.duckflew.service.VoService;
import cn.duckflew.vo.PageRes;
import cn.duckflew.vo.PaidQuestionOrderVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单相关接口
 */
@RestController
@RequestMapping("/order")
public class OrderController
{


    @Autowired
    OrderService orderService;
    /**
     * 分页获取用户订单
     * @apiNote 0->等待支付;1->支付完成2->退款中;3->审核不通过;4->申请退款;5->退款成功
     */
    @GetMapping("/")
    @SaCheckLogin
    public SaResult getUserOrder(
            @RequestParam(required = false,defaultValue = "0")
            Integer pageNum,
            @RequestParam(required = false,defaultValue = "5")
            Integer pageSize
    )
    {
        int userId = StpUtil.getLoginIdAsInt();
        PageRes<PaidQuestionOrderVo> pageRes =orderService.pageUserOrder(pageNum,pageSize,userId);
        return SaResult.ok().setData(pageRes);
    }


    @Autowired
    VoService voService;
    /**
     * 根据订单号查询订单
     * @apiNote 只能查询自己的订单,就算订单号正确但是不是自己的订单号res也是null
     */
    @GetMapping("/{orderId}")
    @SaCheckLogin
    public SaResult getOrderById(
            @PathVariable
            String orderId
    )
    {
        int userId = StpUtil.getLoginIdAsInt();
        Order order = orderService.getOne(new QueryWrapper<Order>().eq("id", orderId).eq("user_id", userId));
        if (order!=null)
        {
            PaidQuestionOrderVo res = voService.orderToVo(order);
            return SaResult.ok().setData(res);
        }
        return SaResult.ok().setMsg("暂无订单");
    }
}
