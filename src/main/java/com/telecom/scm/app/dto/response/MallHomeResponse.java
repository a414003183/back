package com.telecom.scm.app.dto.response;

import java.util.List;

import com.telecom.scm.mall.dto.response.ProductSummaryResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商城首页响应")
public record MallHomeResponse(
        @Schema(description = "分类列表") List<MallCategoryResponse> categories,
        @Schema(description = "Products") List<ProductSummaryResponse> featuredProducts,
        @Schema(description = "Deals") List<ProductSummaryResponse> memberDeals,
        @Schema(description = "Arrivals") List<ProductSummaryResponse> newArrivals) {}
