package com.telecom.scm.mall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商城购物车项响应")
public record MallCartItemResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "GoodsId") Long merchantGoodsId,
        @Schema(description = "SKU ID") Long skuId,
        @Schema(description = "Name") String productName,
        @Schema(description = "Name") String skuName,
        @Schema(description = "Text") String specText,
        @Schema(description = "单价") double unitPrice,
        @Schema(description = "Price") double memberPrice,
        @Schema(description = "UnitPrice") double finalUnitPrice,
        @Schema(description = "数量") int quantity,
        @Schema(description = "Qty") int stockQty,
        @Schema(description = "Amount") double lineAmount,
        @Schema(description = "Amount") double freightAmount,
        @Schema(description = "badge") String badge,
        @Schema(description = "ImageId") Long mainImageId) {}
