package com.telecom.scm.member.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.SaleStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存商家商品请求")
public record SaveMerchantGoodsRequest(
        @NotNull(message = "salePrice is required")
                @DecimalMin(value = "0", message = "salePrice must be greater than or equal to 0")
                @Schema(description = "售价")
                BigDecimal salePrice,
        @DecimalMin(value = "0.00", message = "rebateRate must be greater than or equal 0")
                @Schema(description = "回扣率")
                BigDecimal rebateRate,
        @Schema(description = "销售状态") SaleStatusEnum saleStatus,
        @Schema(description = "库存数量") Integer stockQty,
        @Schema(description = "安全库存") Integer safetyStock,
        @Schema(description = "关键词列表") String keywords,
        @Schema(description = "详情内容") String detailContent,
        @Schema(description = "描述") String description,
        @Schema(description = "主图 ID") Long mainImageId,
        @Schema(description = "配送方式") String deliveryMode) {}
