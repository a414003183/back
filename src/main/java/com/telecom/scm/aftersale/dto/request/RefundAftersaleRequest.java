package com.telecom.scm.aftersale.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "退款售后请求")
public record RefundAftersaleRequest(
        @Schema(description = "Method") String payMethod,
        @Schema(description = "No") String transactionNo,
        @Schema(description = "FileId") Long voucherFileId,
        @Schema(description = "备注") String remark) {}
