package cn.duckflew.enums;

public enum  OrderStatus
{
    /**
     * 等待支付
     */
    TO_PAY(0),
    /**
     * 支付完成
     */
    PAY_SUCCESS(1),
    /**
     * 退款中
     */
    REFUNDING(2),
    /**
     * 审核不通过
     */
    REFUND_FINISH(3),
    /**
     * 申请退款
     */
    REQ_REFUND(4),
    REFUND_SUCCESS(5);
    private int code;

    OrderStatus(int code)
    {
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
