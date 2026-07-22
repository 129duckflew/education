package cn.duckflew.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
public class ConsultAreaIds
{
    /**
     *  感兴趣的领域的id数组
     */
    @NotEmpty(message = "咨询领域数组id不能为空")
    List<String> areaIds;
}
