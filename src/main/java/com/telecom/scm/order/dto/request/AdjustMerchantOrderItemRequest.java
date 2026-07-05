package com.telecom.scm.order.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "调整商家订单项请求")
public record AdjustMerchantOrderItemRequest(
        @NotNull(message = "orderItemId is required") @Schema(description = "订单项 ID")
                Long orderItemId,
        @NotNull(message = "quantity is required")
                @Min(value = 1, message = "quantity must be greater than 0")
                @Schema(description = "数量")
                Integer quantity,
        @NotNull(message = "finalUnitPrice is required")
                @DecimalMin(value = "0", message = "finalUnitPrice cannot be negative")
                @Schema(description = "最终单价")
                BigDecimal finalUnitPrice) {}
