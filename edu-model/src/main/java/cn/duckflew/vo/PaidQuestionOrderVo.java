package cn.duckflew.vo;

import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.pay.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PaidQuestionOrderVo extends Order
{
    @Getter
    @Setter
    private String professorName;

    @Getter
    @Setter
    private List<ConsultArea> areaList;
}
