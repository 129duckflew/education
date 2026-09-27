package cn.duckflew.education.taxonomy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultAreaRequest(
        @NotBlank(message = "领域名称不能为空") @Size(max = 128) String name,
        Long parentId,
        Integer sortOrder
) {
}
