package com.telecom.scm.member.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.SaleStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "导入商家供货请求")
public record ImportMerchantSupplyRequest(
        @NotNull(message = "supplierSkuId is required") @Schema(description = "供应商 SKU ID")
                Long supplierSkuId,
        @NotNull(message = "salePrice is required")
                @DecimalMin(value = "0", message = "salePrice must be greater than or equal to 0")
                @Schema(description = "售价")
                BigDecimal salePrice,
        @DecimalMin(value = "0.00", message = "rebateRate must be greater than or equal to 0")
                @Schema(description = "回扣率")
                BigDecimal rebateRate,
        @Schema(description = "销售状态") SaleStatusEnum saleStatus) {}
