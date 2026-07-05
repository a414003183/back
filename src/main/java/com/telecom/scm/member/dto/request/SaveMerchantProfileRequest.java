package com.telecom.scm.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存商家资料请求")
public record SaveMerchantProfileRequest(
        @NotBlank(message = "shopName is required") @Schema(description = "Name") String shopName,
        @Schema(description = "No") String licenseNo,
        @NotBlank(message = "contactName is required") @Schema(description = "联系人")
                String contactName,
        @NotBlank(message = "contactPhone is required") @Schema(description = "联系人电话")
                String contactPhone,
        @Schema(description = "Desc") String shopDesc,
        @NotNull(message = "status is required") @Schema(description = "状态")
                AccountStatusEnum status) {}
