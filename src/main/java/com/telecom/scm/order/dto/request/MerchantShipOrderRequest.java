package com.telecom.scm.order.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商家Ship订单请求")
public record MerchantShipOrderRequest(
        @NotBlank(message = "carrierName is required") @Schema(description = "Name")
                String carrierName,
        @NotBlank(message = "trackingNo is required") @Schema(description = "跟踪单号")
                String trackingNo,
        @Schema(description = "FileId") Long proofFileId,
        @Schema(description = "Remark") String logisticsRemark) {}
