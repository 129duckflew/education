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
public class LikeAnswerParam
{
    /**
     * 回答的id
     */
    @NotNull(message = "回答id不能为空")
    private Integer answerId;
    /**
     * 点赞状态 1代表点赞 2代表取消了
     */
    @NotNull(message = "点赞状态不能为空")
    @Max(value = 1,message = "点赞状态值只能为0/1")
    @Min(value = 0,message = "点赞状态值只能为0/1")
    private Integer status;
}
