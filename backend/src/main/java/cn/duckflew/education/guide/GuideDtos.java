package cn.duckflew.education.guide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class GuideDtos {

    private GuideDtos() {
    }

    public record GuideNode(Long id, String name, Long parentId, boolean important,
                            int sortOrder, List<GuideNode> children) {
    }

    public record SaveRequest(
            @NotBlank(message = "节点名称不能为空") @Size(max = 255) String name,
            Long parentId,
            Boolean important,
            Integer sortOrder
    ) {
    }

    public record AreasRequest(@NotNull List<Long> areaIds) {
    }
}
