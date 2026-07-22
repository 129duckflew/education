package cn.duckflew.vo;

import cn.duckflew.entity.ConsultArea;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultAreaVo extends ConsultArea
{

    /**
     *  子级领域
     */
    List<ConsultAreaVo> childrenAreas;

    public ConsultAreaVo(ConsultArea consultArea)
    {
        setAreaName(consultArea.getAreaName());
        setParentId(consultArea.getParentId());
        setId(consultArea.getId());
    }
}
