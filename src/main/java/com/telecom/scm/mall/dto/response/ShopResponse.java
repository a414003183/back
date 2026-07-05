package com.telecom.scm.mall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "店铺响应")
public record ShopResponse(
        @Schema(description = "ID") Long id,
        @Schema(description = "Name") String shopName,
        @Schema(description = "Desc") String shopDesc,
        @Schema(description = "联系人") String contactName,
        @Schema(description = "联系人电话") String contactPhone,
        @Schema(description = "状态") String status,
        @Schema(description = "Count") Long productCount,
        @Schema(description = "Sales") Long totalSales) {}
