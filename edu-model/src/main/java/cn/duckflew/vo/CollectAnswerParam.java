package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectAnswerParam
{
    /**
     * 回答的id
     */
    @NotNull(message = "回答id不能为空")
    private Integer answerId;
    /**
     * 收藏状态, 0为取消收藏，1为收藏
     */
    @NotNull(message = "收藏状态不能为空")
    @Max(value = 1,message = "收藏状态值只能为0/1")
    @Min(value = 0,message = "收藏状态值只能为0/1")
    private Integer collectStatus;
}
