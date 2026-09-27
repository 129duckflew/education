package cn.duckflew.education.common.api;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * 分页响应体。
 */
public record PageResponse<T>(List<T> list, long total, int page, int size) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getTotalElements(),
                page.getNumber(), page.getSize());
    }

    public static <S, T> PageResponse<T> of(Page<S> page, Function<S, T> mapper) {
        return new PageResponse<>(page.getContent().stream().map(mapper).toList(),
                page.getTotalElements(), page.getNumber(), page.getSize());
    }

    public static <T> PageResponse<T> empty(int page, int size) {
        return new PageResponse<>(List.of(), 0, page, size);
    }
}
