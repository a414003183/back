package com.telecom.scm.member.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.SaleStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** MerchantGoodsEntity 实体，映射 merchant_goods 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_goods")
public class MerchantGoodsEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "merchant goods id")
    private Long merchantGoodsId;

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "供应商 ID")
    private Long supplierId;

    @Schema(description = "supplier sku id")
    private Long supplierSkuId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "sale price")
    private BigDecimal salePrice;

    @Schema(description = "current cost price")
    private BigDecimal currentCostPrice;

    @Schema(description = "运费金额")
    private BigDecimal freightAmount;

    @Schema(description = "rebate rate")
    private BigDecimal rebateRate;

    @Schema(description = "sale status")
    private SaleStatusEnum saleStatus;

    @Schema(description = "delivery mode")
    private String deliveryMode;

    @Schema(description = "stock qty")
    private Integer stockQty;

    @Schema(description = "safety stock")
    private Integer safetyStock;

    @Schema(description = "main image id")
    private Long mainImageId;

    @Schema(description = "image ids")
    private String imageIds;

    @Schema(description = "keywords")
    private String keywords;

    @Schema(description = "detail content")
    private String detailContent;

    @Schema(description = "description")
    private String description;

    @Schema(description = "operator id")
    private Long operatorId;
}
