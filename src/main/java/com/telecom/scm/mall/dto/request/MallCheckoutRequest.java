package com.telecom.scm.mall.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商城结算请求")
public record MallCheckoutRequest(
        @Schema(description = "收件人姓名") String receiverName,
        @Schema(description = "收件人电话") String receiverPhone,
        @Schema(description = "Province") String receiverProvince,
        @Schema(description = "City") String receiverCity,
        @Schema(description = "District") String receiverDistrict,
        @Schema(description = "收件人地址") String receiverAddress,
        @Schema(description = "Method") String payMethod,
        @Schema(description = "Remark") String customerRemark,
        @Schema(description = "FileId") Long contractFileId,
        @Schema(description = "Points") Boolean usePoints,
        @Schema(description = "MerchantGoodsIds") List<Long> selectedMerchantGoodsIds) {}
