package com.telecom.scm.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "客户地址响应")
public record CustomerAddressResponse(
        @Schema(description = "收件人姓名") String receiverName,
        @Schema(description = "收件人电话") String receiverPhone,
        @Schema(description = "Province") String receiverProvince,
        @Schema(description = "City") String receiverCity,
        @Schema(description = "District") String receiverDistrict,
        @Schema(description = "收件人地址") String receiverAddress) {}
