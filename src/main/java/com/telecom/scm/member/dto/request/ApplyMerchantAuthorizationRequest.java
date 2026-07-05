package com.telecom.scm.member.dto.request;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Apply商家Authorization请求")
public record ApplyMerchantAuthorizationRequest(
        @NotNull(message = "supplierId is required") @Schema(description = "供应商 ID")
                Long supplierId,
        @NotNull(message = "supplierSkuId is required") @Schema(description = "SkuId")
                Long supplierSkuId,
        @Schema(description = "备注") String remark) {}
