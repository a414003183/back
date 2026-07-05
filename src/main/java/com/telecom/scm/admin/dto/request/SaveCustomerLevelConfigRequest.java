package com.telecom.scm.admin.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存客户等级配置请求")
public record SaveCustomerLevelConfigRequest(
        @NotBlank(message = "levelCode is required") @Schema(description = "等级编码") String levelCode,
        @NotBlank(message = "levelName is required") @Schema(description = "等级名称") String levelName,
        @NotNull(message = "upgradeThresholdAmount is required")
                @DecimalMin(
                        value = "0",
                        message = "upgradeThresholdAmount must be greater than or equal to 0")
                @Schema(description = "升级门槛金额")
                BigDecimal upgradeThresholdAmount,
        @Schema(description = "排序号") Integer sortNo,
        @NotNull(message = "status is required") @Schema(description = "状态")
                AccountStatusEnum status,
        @Schema(description = "备注") String remark) {}
