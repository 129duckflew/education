package cn.duckflew.education.taxonomy;

import java.util.List;

public record ConsultAreaNode(
        Long id,
        String name,
        Long parentId,
        int sortOrder,
        List<ConsultAreaNode> children
) {
}
