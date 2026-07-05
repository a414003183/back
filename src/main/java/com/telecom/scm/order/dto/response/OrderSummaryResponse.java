package com.telecom.scm.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "订单摘要响应")
public record OrderSummaryResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "订单编号") String orderNo,
        @Schema(description = "Name") String customerName,
        @Schema(description = "Name") String merchantName,
        @Schema(description = "金额") double amount,
        @Schema(description = "状态") String status,
        @Schema(description = "Status") String payStatus,
        @Schema(description = "物流单号") String logisticsNo,
        @Schema(description = "At") String createdAt,
        @Schema(description = "Status") String aftersaleStatus,
        @Schema(description = "Source") String orderSource) {}
