package com.telecom.scm.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存品牌请求")
public record SaveBrandRequest(
        @Schema(description = "ID") Long id,
        @NotBlank(message = "brandName is required") @Schema(description = "Name") String brandName,
        @Schema(description = "Desc") String brandDesc,
        @Schema(description = "状态") AccountStatusEnum status,
        @Schema(description = "No") Integer sortNo) {}
