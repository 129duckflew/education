package cn.duckflew.controller;

import cn.duckflew.config.AliPayConfig;
import cn.duckflew.entity.pay.Order;
import cn.duckflew.enums.OrderStatus;
import cn.duckflew.exception.OrderIdInvalidException;
import cn.duckflew.mapper.pay.OrderMapper;
import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
@Slf4j
/**
 * 支付相关接口
 */
public class AliPayController
{

    private static final String GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    //签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private OrderMapper orderMapper;

    /**
     * 提供订单号发起支付
     * @param orderId 订单号
     * @throws Exception
     */
    @GetMapping("/pay") // &subject=xxx&traceNo=xxx&totalAmount=xxx
    public void pay(String orderId, HttpServletResponse httpResponse) throws Exception {
        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
        Order order = orderMapper.selectById(orderId);
        if (order==null)throw new OrderIdInvalidException("订单id无效",orderId);
        log.info("付费接口,订单信息:{}",order.toString());
        // 2. 创建 Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", order.getId());  // 我们自己生成的订单编号
        bizContent.set("total_amount", order.getTotal()); // 订单的总金额
        bizContent.set("subject", order.getOrderName());   // 支付的名称
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());
        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    /**
     * 支付宝回调接口
     * @param request
     * @return
     * @throws Exception
     * @apiNote 这个接口你可以忽略
     */
    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            log.info("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }

            String outTradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");
            String alipayTradeNo = params.get("trade_no");

            log.info("outTradeNo:{}",outTradeNo);
            log.info("gmtPayment:{}",gmtPayment);
            log.info("alipayTradeNo:{}",alipayTradeNo);
            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8"); // 验证签名
            // 支付宝验签
            if (checkSignature) {
                // 验签通过
                log.info("交易名称:{}",params.get("subject"));
                log.info("交易状态:{}" , params.get("trade_status"));
                log.info("支付宝交易凭证号:{}" , params.get("trade_no"));
                log.info("商户订单号:{}" , params.get("out_trade_no"));
                log.info("交易金额:{}", params.get("total_amount"));
                log.info("买家在支付宝唯一id:{}" , params.get("buyer_id"));
                log.info("买家付款时间:{}" , params.get("gmt_payment"));
                log.info("买家付款金额:{}" , params.get("buyer_pay_amount"));

                // 查询订单
                Order order = orderMapper.selectById(outTradeNo);
                if (order != null) {
                    log.info("支付宝单号回写到订单表中");
                    order.setAliOrderId(alipayTradeNo);
                    order.setPayTime(new Date());
                    order.setOrderStatus(OrderStatus.PAY_SUCCESS.getCode());
                    orderMapper.updateById(order);
                }
            }
        }
        return "success";
    }

}


