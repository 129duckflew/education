package cn.duckflew.vo;

import cn.duckflew.annotation.FreeQuestionGroup;
import cn.duckflew.annotation.PaidQuestionGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class SubmitQuestionParam
{
    /**
     * 问题标题
     */
    @NotBlank(message = "问题标题不能为空",groups = {PaidQuestionGroup.class, FreeQuestionGroup.class})
    @Length(max = 50,message = "问题标题最大长度50",groups = {PaidQuestionGroup.class, FreeQuestionGroup.class})
    private String questionTitle;
    /**
     * 问题描述
     */
    @Length(max = 500,message = "问题描述最多500字",groups = {PaidQuestionGroup.class, FreeQuestionGroup.class})
    private String questionDesc;
    /**
     * 教授id数组
     */
    @NotNull(message = "教授id数组不能为空",groups = {PaidQuestionGroup.class, FreeQuestionGroup.class})
    @Size(max = 1,message = "一次只能对一个教授发起付费提问",groups = PaidQuestionGroup.class)
    private List<Integer> professorIds;
    /**
     * 咨询领域id数组
     */
    @NotEmpty(message = "咨询领域id数组不能为空",groups = {PaidQuestionGroup.class, FreeQuestionGroup.class})
    private List<String> consultAreaIdList;
    /**
     * 问题描述图片
     * @since 传入fileId
     */
    private List<String> questionDescImgList;
}
