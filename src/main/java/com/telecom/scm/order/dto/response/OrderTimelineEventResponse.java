package com.telecom.scm.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "订单时间线事件响应")
public record OrderTimelineEventResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "事件类型") String eventType,
        @Schema(description = "标题") String title,
        @Schema(description = "描述") String description,
        @Schema(description = "Name") String operatorName,
        @Schema(description = "事件时间") String eventTime) {}
