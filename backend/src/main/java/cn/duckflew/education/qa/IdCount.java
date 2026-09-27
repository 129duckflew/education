package cn.duckflew.education.qa;

/**
 * 通用「id → 计数」投影，用于批量统计避免 N+1。
 */
public record IdCount(Long id, long count) {
}
