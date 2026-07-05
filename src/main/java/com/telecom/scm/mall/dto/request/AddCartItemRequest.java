package com.telecom.scm.mall.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "添加购物车项请求")
public record AddCartItemRequest(
        @NotNull(message = "merchantGoodsId is required") @Schema(description = "商家商品 ID")
                Long merchantGoodsId,
        @Min(value = 1, message = "quantity must be at least 1") @Schema(description = "数量")
                int quantity) {}
