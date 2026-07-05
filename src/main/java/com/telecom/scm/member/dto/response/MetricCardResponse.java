package com.telecom.scm.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "指标卡片响应")
public record MetricCardResponse(
        @Schema(description = "标签") String label,
        @Schema(description = "值") String value,
        @Schema(description = "trend") String trend) {

    public static MetricCardResponse of(String label, String value, String trend) {
        return new MetricCardResponse(label, value, trend);
    }
}
