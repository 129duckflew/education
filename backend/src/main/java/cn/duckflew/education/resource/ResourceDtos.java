package cn.duckflew.education.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class ResourceDtos {

    private ResourceDtos() {
    }

    public record CreateRequest(
            @NotBlank(message = "资源名称不能为空") @Size(max = 255) String name,
            @NotNull(message = "必须指定文件") Long fileId,
            @Size(max = 1000) String remark
    ) {
    }

    public record ResourceView(
            Long id,
            String name,
            Long fileId,
            String remark,
            Long professorId,
            String professorName,
            Instant createdAt
    ) {
    }
}
