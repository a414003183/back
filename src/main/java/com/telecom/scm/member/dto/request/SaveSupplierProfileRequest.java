package com.telecom.scm.member.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存供应商资料请求")
public record SaveSupplierProfileRequest(
        @NotBlank(message = "supplierName is required") @Schema(description = "Name")
                String supplierName,
        @NotBlank(message = "contactName is required") @Schema(description = "联系人")
                String contactName,
        @NotBlank(message = "contactPhone is required") @Schema(description = "联系人电话")
                String contactPhone,
        @Schema(description = "Desc") String supplyDesc,
        @Schema(description = "FileId") Long qualificationFileId) {}
