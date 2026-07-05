package com.telecom.scm.app.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.telecom.scm.app.mapper.CustomerOrderItemRow;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "客户订单详情响应")
public record CustomerOrderDetailResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "订单编号") String orderNo,
        @Schema(description = "Name") String customerName,
        @Schema(description = "Name") String merchantName,
        @Schema(description = "Status") String orderStatus,
        @Schema(description = "Status") String payStatus,
        @Schema(description = "Status") String shipmentStatus,
        @Schema(description = "Status") String aftersaleStatus,
        @Schema(description = "Amount") BigDecimal goodsAmount,
        @Schema(description = "Amount") BigDecimal freightAmount,
        @Schema(description = "折扣金额") BigDecimal discountAmount,
        @Schema(description = "支付金额") BigDecimal payAmount,
        @Schema(description = "收件人姓名") String receiverName,
        @Schema(description = "收件人电话") String receiverPhone,
        @Schema(description = "收件人地址") String receiverAddress,
        @Schema(description = "Remark") String customerRemark,
        @Schema(description = "At") String createdAt,
        @Schema(description = "项列表") List<CustomerOrderItemRow> items) {}
