package com.telecom.scm.pricing.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存客户价格规则请求")
public record SaveCustomerPriceRuleRequest(
        @NotNull(message = "customerId is required") @Schema(description = "客户 ID") Long customerId,
        @NotNull(message = "skuId is required") @Schema(description = "SKU ID") Long skuId,
        @NotNull(message = "specialPrice is required")
                @DecimalMin(value = "0.01", message = "specialPrice must be positive")
                @Schema(description = "特价")
                BigDecimal specialPrice,
        @NotNull(message = "status is required") @Schema(description = "状态")
                AccountStatusEnum status,
        @Schema(description = "备注") String remark) {}
