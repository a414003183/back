package com.telecom.scm.mall.dto.response;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "产品详情响应")
public record ProductDetailResponse(
        @Schema(description = "ID") Long id,
        @Schema(description = "商家 ID") Long merchantId,
        @Schema(description = "GoodsId") Long merchantGoodsId,
        @Schema(description = "SKU ID") Long skuId,
        @Schema(description = "名称") String name,
        @Schema(description = "品牌") String brand,
        @Schema(description = "分类") String category,
        @Schema(description = "Category") String parentCategory,
        @Schema(description = "Category") String rootCategory,
        @Schema(description = "摘要") String summary,
        @Schema(description = "价格") double price,
        @Schema(description = "Price") double memberPrice,
        @Schema(description = "Amount") double freightAmount,
        @Schema(description = "库存") int stock,
        @Schema(description = "specs") String specs,
        @Schema(description = "Time") String leadTime,
        @Schema(description = "badge") String badge,
        @Schema(description = "单位") String unit,
        @Schema(description = "Note") String deliveryNote,
        @Schema(description = "Rule") String pricingRule,
        @Schema(description = "ImageId") Long mainImageId,
        @Schema(description = "Ids") String imageIds,
        @Schema(description = "关键词列表") String keywords,
        @Schema(description = "Content") String detailContent,
        @Schema(description = "描述") String description,
        @Schema(description = "Name") String shopName,
        @Schema(description = "Count") Long saleCount,
        @Schema(description = "当前等级") String currentLevel,
        @Schema(description = "客户等级信息") // 新增：客户等级信息
                String currentLevelName,
        @Schema(description = "DiscountValue") Double currentDiscountValue,
        @Schema(description = "Prices") List<Map<String, Object>> levelPrices) {}
