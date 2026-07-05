package com.telecom.scm.member.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存供应商授权请求")
public record SaveSupplierAuthorizationRequest(
        @NotNull(message = "merchantId is required") @Schema(description = "商家 ID") Long merchantId,
        @NotNull(message = "supplierSkuId is required") @Schema(description = "供应商 SKU ID")
                Long supplierSkuId,
        @NotNull(message = "authorizedPrice is required")
                @DecimalMin(
                        value = "0",
                        message = "authorizedPrice must be greater than or equal to 0")
                @Schema(description = "授权价格")
                BigDecimal authorizedPrice,
        @Schema(description = "分配库存数量") Integer allocatedStockQty,
        @Schema(description = "授权状态") String authStatus,
        @Schema(description = "备注") String remark) {}
