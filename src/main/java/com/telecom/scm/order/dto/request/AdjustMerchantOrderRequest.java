package com.telecom.scm.order.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "调整商家订单请求")
public record AdjustMerchantOrderRequest(
        @NotEmpty(message = "items cannot be empty") @Schema(description = "项列表")
                List<@Valid AdjustMerchantOrderItemRequest> items,
        @Schema(description = "备注") String remark) {}
