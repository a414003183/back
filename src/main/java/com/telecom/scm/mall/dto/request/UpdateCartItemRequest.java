package com.telecom.scm.mall.dto.request;

import jakarta.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "更新购物车项请求")
public record UpdateCartItemRequest(
        @Min(value = 1, message = "quantity must be at least 1") @Schema(description = "数量")
                int quantity) {}
