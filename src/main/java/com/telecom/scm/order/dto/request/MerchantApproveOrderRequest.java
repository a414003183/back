package com.telecom.scm.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商家审批订单请求")
public record MerchantApproveOrderRequest(@Schema(description = "备注") String remark) {}
