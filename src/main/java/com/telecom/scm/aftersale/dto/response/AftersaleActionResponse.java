package com.telecom.scm.aftersale.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "售后操作响应")
public record AftersaleActionResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "No") String aftersaleNo,
        @Schema(description = "Status") String aftersaleStatus,
        @Schema(description = "TrackingNo") String returnTrackingNo) {}
