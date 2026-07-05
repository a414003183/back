package com.telecom.scm.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "订单操作响应")
public record OrderActionResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "订单编号") String orderNo,
        @Schema(description = "Status") String orderStatus,
        @Schema(description = "Status") String payStatus,
        @Schema(description = "物流单号") String logisticsNo) {}
