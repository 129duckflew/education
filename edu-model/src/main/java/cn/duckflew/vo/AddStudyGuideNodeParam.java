package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStudyGuideNodeParam
{
    /**
     * 父节点id
     */
    @NotNull(message = "父节点id不能为空")
    @Pattern(regexp = "\\d(.\\d)*",message = "parentId不符合规范")
    private String parentId;
    /**
     * 节点名
     */
    @NotBlank(message = "节点名不能为空")
    private String nodeName;
    /**
     * 是否是重点 1是重点 0是非重点
     */
    @NotNull(message = "是否是重点不能为空")
    private Integer isImportant;
}
