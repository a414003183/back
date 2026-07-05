package com.telecom.scm.order.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "创建订单请求")
public record CreateOrderRequest(
        @NotBlank(message = "receiverName is required") @Schema(description = "收件人姓名")
                String receiverName,
        @NotBlank(message = "receiverPhone is required") @Schema(description = "收件人电话")
                String receiverPhone,
        @NotBlank(message = "receiverProvince is required") @Schema(description = "Province")
                String receiverProvince,
        @NotBlank(message = "receiverCity is required") @Schema(description = "City")
                String receiverCity,
        @NotBlank(message = "receiverDistrict is required") @Schema(description = "District")
                String receiverDistrict,
        @NotBlank(message = "receiverAddress is required") @Schema(description = "收件人地址")
                String receiverAddress,
        @NotBlank(message = "payMethod is required") @Schema(description = "Method")
                String payMethod,
        @Schema(description = "Remark") String customerRemark,
        @Schema(description = "FileId") Long contractFileId,
        @Schema(description = "Points") Boolean usePoints,
        @Valid @NotEmpty(message = "items cannot be empty") @Schema(description = "项列表")
                List<CreateOrderItemRequest> items) {}
