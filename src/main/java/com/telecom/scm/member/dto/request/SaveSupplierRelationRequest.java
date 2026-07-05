package com.telecom.scm.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存供应商Relation请求")
public record SaveSupplierRelationRequest(
        @NotNull(message = "merchantId is required") @Schema(description = "商家 ID") Long merchantId,
        @NotBlank(message = "status is required") @Schema(description = "状态") String status,
        @Schema(description = "备注") String remark) {}
