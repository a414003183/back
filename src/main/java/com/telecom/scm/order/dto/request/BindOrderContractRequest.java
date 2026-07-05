package com.telecom.scm.order.dto.request;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "绑定订单合同请求")
public record BindOrderContractRequest(
        @NotNull(message = "fileId is required") @Schema(description = "Id") Long fileId) {}
