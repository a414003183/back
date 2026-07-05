package com.telecom.scm.order.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登记订单支付请求")
public record RegisterOrderPaymentRequest(
        @NotBlank(message = "payMethod is required") @Schema(description = "Method")
                String payMethod,
        @NotBlank(message = "transactionNo is required") @Schema(description = "No")
                String transactionNo,
        @Schema(description = "FileId") Long voucherFileId,
        @Schema(description = "备注") String remark) {}
