package com.telecom.scm.member.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.SaleStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "保存商家产品请求")
public record SaveMerchantProductRequest(
        @NotNull(message = "brandId is required") @Schema(description = "品牌 ID") Long brandId,
        @NotNull(message = "categoryId is required") @Schema(description = "分类 ID") Long categoryId,
        @NotBlank(message = "spuName is required") @Schema(description = "SPU 名称") String spuName,
        @NotBlank(message = "skuName is required") @Schema(description = "SKU 名称") String skuName,
        @NotBlank(message = "specText is required") @Schema(description = "规格文本") String specText,
        @NotNull(message = "salePrice is required")
                @DecimalMin(value = "0", message = "salePrice must be greater than or equal to 0")
                @Schema(description = "售价")
                BigDecimal salePrice,
        @NotNull(message = "stockQty is required")
                @Min(value = 0, message = "stockQty must be greater than or equal to 0")
                @Schema(description = "库存数量")
                Integer stockQty,
        @NotNull(message = "safetyStock is required")
                @Min(value = 0, message = "safetyStock must be greater than or equal to 0")
                @Schema(description = "安全库存")
                Integer safetyStock,
        @DecimalMin(value = "0.00", message = "freightAmount must be greater than or equal to 0")
                @Schema(description = "运费金额")
                BigDecimal freightAmount,
        @Schema(description = "描述") String description,
        @Schema(description = "关键词列表") String keywords,
        @Schema(description = "详情内容") String detailContent,
        @Schema(description = "主图 ID") Long mainImageId,
        @Schema(description = "图片 ID 列表") List<Long> imageIds,
        @Schema(description = "销售状态") SaleStatusEnum saleStatus,
        @Schema(description = "配送方式") String deliveryMode) {}
