package com.telecom.scm.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "订单创建响应")
public record OrderCreateResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "订单编号") String orderNo,
        @Schema(description = "支付金额") double payAmount,
        @Schema(description = "Points") int usedPoints,
        @Schema(description = "DeductionAmount") double pointsDeductionAmount,
        @Schema(description = "Status") String orderStatus,
        @Schema(description = "Status") String payStatus) {}
