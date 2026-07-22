package cn.duckflew.vo;

import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudyGuideParam
{
    /**
     *
     */
    @NotBlank(message = "修改必须指定id",groups = UpdateGroup.class)
    @Null(message = "新增不能指定id",groups = AddGroup.class)
    private String studyGuideId;
    /**
     * 学习导图的名字
     */
    @NotBlank(message = "学习导图名不能为空",groups = AddGroup.class)
    private String studyGuideName;
    /**
     * 学习导图相关领域的id
     */
    @NotEmpty(message = "相关领域id数组不能为空",groups = {AddGroup.class})
    private List<String> consultAreaIdList;

}
