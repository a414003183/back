package com.telecom.scm.app.dto.response;

import java.util.List;

import com.telecom.scm.mall.dto.response.ProductSummaryResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商城产品列表响应")
public record MallProductListResponse(
        @Schema(description = "列表") List<ProductSummaryResponse> list,
        @Schema(description = "总数") int total,
        @Schema(description = "页码") int page,
        @Schema(description = "每页大小") int pageSize) {}
