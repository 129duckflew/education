package cn.duckflew.education.user.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record InterestAreaRequest(@NotEmpty(message = "至少选择一个领域") List<Long> areaIds) {
}
