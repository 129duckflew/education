package cn.duckflew.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRoleParam
{
    /**
     * 名称代码
     */
    @NotBlank(message = "名称代码不能为空")
    private String name;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String nameZh;

    private List<Integer> menuIdList;
}
