package com.telecom.scm.member.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存客户资料请求")
public record SaveCustomerProfileRequest(
        @Schema(description = "公司名称") String companyName,
        @NotBlank(message = "contactName is required") @Schema(description = "联系人")
                String contactName,
        @NotBlank(message = "contactPhone is required") @Schema(description = "联系人电话")
                String contactPhone,
        @Schema(description = "Title") String invoiceTitle,
        @Schema(description = "税号") String taxNo,
        @Schema(description = "开户行") String bankName,
        @Schema(description = "银行账号") String bankAccount,
        @NotBlank(message = "receiverProvince is required") @Schema(description = "Province")
                String receiverProvince,
        @NotBlank(message = "receiverCity is required") @Schema(description = "City")
                String receiverCity,
        @NotBlank(message = "receiverDistrict is required") @Schema(description = "District")
                String receiverDistrict,
        @NotBlank(message = "receiverAddress is required") @Schema(description = "收件人地址")
                String receiverAddress) {}
