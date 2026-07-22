package cn.duckflew.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Data
public class AddConsultAreaParams
{
    /**
     * 咨询领域名称
     */
    @NotBlank(message = "咨询领域名称不能为空")
    private String areaName;
    /**
     * 父领域节点ID
     * @since 如果想添加顶级咨询领域 设置parentId为0
     */
    @NotNull(message = "父领域节点ID不能为空")
    private Integer parentId;
}
