package com.telecom.scm.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存分类请求")
public record SaveCategoryRequest(
        @Schema(description = "ID") Long id,
        @NotBlank(message = "categoryName is required") @Schema(description = "Name")
                String categoryName,
        @Schema(description = "父级 ID") Long parentId,
        @NotNull(message = "status is required") @Schema(description = "状态")
                AccountStatusEnum status,
        @Schema(description = "No") Integer sortNo) {}
