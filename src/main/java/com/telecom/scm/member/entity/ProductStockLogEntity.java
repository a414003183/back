package com.telecom.scm.member.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 商品库存变更日志实体，对应 product_stock_log 表。 供订单、售后等模块共用。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_stock_log")
public class ProductStockLogEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "变更类型")
    private String changeType;

    @Schema(description = "变更数量")
    private Integer changeQty;

    @Schema(description = "变更前数量")
    private Integer beforeQty;

    @Schema(description = "变更后数量")
    private Integer afterQty;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务ID")
    private Long bizId;

    @Schema(description = "操作人ID")
    private Long operatedBy;
}
