package com.telecom.scm.points.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "积分Deduction快照")
public record PointDeductionSnapshot(
        @Schema(description = "Points") int usedPoints,
        @Schema(description = "Amount") BigDecimal deductionAmount) {}
