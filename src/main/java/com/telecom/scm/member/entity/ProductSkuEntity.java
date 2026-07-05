package com.telecom.scm.member.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** ProductSkuEntity 实体，映射 product_sku 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_sku")
public class ProductSkuEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "sku code")
    private String skuCode;

    @Schema(description = "sku name")
    private String skuName;

    @Schema(description = "spec text")
    private String specText;

    @Schema(description = "base price")
    private BigDecimal basePrice;

    @Schema(description = "stock qty")
    private Integer stockQty;

    @Schema(description = "safety stock")
    private Integer safetyStock;

    @Schema(description = "operator id")
    private Long operatorId;
}
