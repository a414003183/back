package com.telecom.scm.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "创建订单项请求")
public record CreateOrderItemRequest(
        @NotNull(message = "merchantGoodsId is required") @Schema(description = "商家商品 ID")
                Long merchantGoodsId,
        @NotNull(message = "skuId is required") @Schema(description = "SKU ID") Long skuId,
        @NotNull(message = "quantity is required")
                @Min(value = 1, message = "quantity must be greater than 0")
                @Schema(description = "数量")
                Integer quantity) {}
