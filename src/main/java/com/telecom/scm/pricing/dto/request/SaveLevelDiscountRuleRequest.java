package com.telecom.scm.pricing.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存等级折扣规则请求")
public record SaveLevelDiscountRuleRequest(
        @NotBlank(message = "memberLevel is required") @Schema(description = "会员等级")
                String memberLevel,
        @NotBlank(message = "targetType is required") @Schema(description = "目标类型")
                String targetType,
        @NotNull(message = "targetId is required") @Schema(description = "目标 ID") Long targetId,
        @NotNull(message = "discountRate is required")
                @DecimalMin(value = "0.01", message = "discountRate must be positive")
                @Schema(description = "折扣率")
                BigDecimal discountRate,
        @NotNull(message = "status is required") @Schema(description = "状态")
                AccountStatusEnum status,
        @Schema(description = "备注") String remark) {}
