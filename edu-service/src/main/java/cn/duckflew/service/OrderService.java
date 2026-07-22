package cn.duckflew.service;

import cn.duckflew.entity.pay.Order;
import cn.duckflew.mapper.pay.OrderMapper;
import cn.duckflew.vo.PageRes;
import cn.duckflew.vo.PaidQuestionOrderVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order>
{

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    VoService voService;

    /**
     * 分页查询个人订单
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    public PageRes<PaidQuestionOrderVo> pageUserOrder(Integer pageNum, Integer pageSize,Integer userId)
    {
        PageRes<PaidQuestionOrderVo> res = new PageRes<>();
        Page<Order> orderPage = new Page<>(pageNum,pageSize);
        orderMapper.selectPage(orderPage,
                new QueryWrapper<Order>().eq("user_id",userId));
        res.setTotal(orderPage.getTotal());
        res.setList(orderPage.getRecords().stream().map(order->voService.orderToVo(order)).collect(Collectors.toList()));
        return res;
    }
}
