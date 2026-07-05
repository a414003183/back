package com.telecom.scm.aftersale.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登记退货发货请求")
public record RegisterReturnShipmentRequest(
        @NotBlank(message = "returnTrackingNo is required") @Schema(description = "TrackingNo")
                String returnTrackingNo,
        @Schema(description = "FileId") Long proofFileId,
        @Schema(description = "备注") String remark) {}
