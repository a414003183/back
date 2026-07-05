package com.telecom.scm.order.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 订单商品项实体，映射 order_item 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_item")
public class OrderItemEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "商家商品 ID")
    private Long merchantGoodsId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SPU 名称")
    private String spuName;

    @Schema(description = "SKU 名称")
    private String skuName;

    @Schema(description = "规格文本")
    private String specText;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "基础售价")
    private BigDecimal basePrice;

    @Schema(description = "会员价")
    private BigDecimal memberPrice;

    @Schema(description = "客户专属价")
    private BigDecimal customerPrice;

    @Schema(description = "成交价")
    private BigDecimal finalUnitPrice;

    @Schema(description = "成交金额")
    private BigDecimal finalAmount;

    @Schema(description = "成本价")
    private BigDecimal costPrice;

    @Schema(description = "利润金额")
    private BigDecimal profitAmount;
}
