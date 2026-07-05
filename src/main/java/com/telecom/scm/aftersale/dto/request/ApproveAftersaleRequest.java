package com.telecom.scm.aftersale.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "审批售后请求")
public record ApproveAftersaleRequest(
        @DecimalMin(value = "0.01", message = "approvedAmount must be greater than 0")
                @Schema(description = "审批金额")
                BigDecimal approvedAmount,
        @Schema(description = "备注") String remark) {}
