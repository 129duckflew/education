package cn.duckflew.entity.pay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_order")
public class Order
{
    /**
     * 订单id
     */
    private String id;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 支付宝订单id
     */
    private String aliOrderId;
    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;
    /**
     * 订单支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date payTime;
    /**
     * 订单总价
     */
    private BigDecimal total;

    /**
     * 订单名称
     */
    private String orderName;
    /**
     * 用户id
     */
    private Integer userId;
}
