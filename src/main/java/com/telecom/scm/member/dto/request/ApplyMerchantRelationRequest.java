package com.telecom.scm.member.dto.request;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Apply商家Relation请求")
public record ApplyMerchantRelationRequest(
        @NotNull(message = "supplierId is required") @Schema(description = "供应商 ID")
                Long supplierId,
        @Schema(description = "备注") String remark) {}
