package com.telecom.scm.aftersale.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "创建售后请求")
public record CreateAftersaleRequest(
        @NotNull(message = "orderId is required") @Schema(description = "订单 ID") Long orderId,
        @NotBlank(message = "aftersaleType is required") @Schema(description = "售后类型")
                String aftersaleType,
        @NotBlank(message = "reasonType is required") @Schema(description = "原因类型")
                String reasonType,
        @Schema(description = "原因描述") String reasonDesc,
        @Schema(description = "附件文件 ID") Long attachmentFileId,
        @NotNull(message = "applyAmount is required")
                @DecimalMin(value = "0.01", message = "applyAmount must be greater than 0")
                @Schema(description = "申请金额")
                BigDecimal applyAmount) {}
