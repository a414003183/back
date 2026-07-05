package com.telecom.scm.aftersale.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "售后摘要响应")
public record AftersaleSummaryResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "No") String aftersaleNo,
        @Schema(description = "订单编号") String orderNo,
        @Schema(description = "Name") String customerName,
        @Schema(description = "Name") String merchantName,
        @Schema(description = "Type") String aftersaleType,
        @Schema(description = "Amount") double applyAmount,
        @Schema(description = "Status") String aftersaleStatus,
        @Schema(description = "Return") boolean needReturn,
        @Schema(description = "TrackingNo") String returnTrackingNo,
        @Schema(description = "备注") String remark,
        @Schema(description = "At") String createdAt) {}
