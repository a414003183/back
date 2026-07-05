package com.telecom.scm.pricing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存商品授权规则请求")
public record SaveGoodsAuthRuleRequest(
        @NotBlank(message = "authType is required") @Schema(description = "授权类型") String authType,
        @NotNull(message = "targetId is required") @Schema(description = "目标 ID") Long targetId,
        @NotNull(message = "status is required") @Schema(description = "状态")
                AccountStatusEnum status,
        @Schema(description = "备注") String remark) {}
