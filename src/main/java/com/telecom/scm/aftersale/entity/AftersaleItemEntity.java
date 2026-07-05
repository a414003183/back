package com.telecom.scm.aftersale.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 售后商品项实体，对应 aftersale_item 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("aftersale_item")
public class AftersaleItemEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "售后单 ID")
    private Long aftersaleId;

    @Schema(description = "order item id")
    private Long orderItemId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "spu name")
    private String spuName;

    @Schema(description = "sku name")
    private String skuName;

    @Schema(description = "spec text")
    private String specText;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "apply amount")
    private BigDecimal applyAmount;
}
