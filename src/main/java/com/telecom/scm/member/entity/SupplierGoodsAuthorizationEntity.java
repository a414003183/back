package com.telecom.scm.member.entity;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** SupplierGoodsAuthorizationEntity 实体，映射 supplier_goods_authorization 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("supplier_goods_authorization")
public class SupplierGoodsAuthorizationEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "authorization id")
    private Long authorizationId;

    @Schema(description = "供应商 ID")
    private Long supplierId;

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "supplier sku id")
    private Long supplierSkuId;

    @Schema(description = "auth status")
    private String authStatus;

    @Schema(description = "authorized price")
    private BigDecimal authorizedPrice;

    @Schema(description = "allocated stock qty")
    private Integer allocatedStockQty;

    @Schema(description = "authorized at")
    private LocalDateTime authorizedAt;

    @Schema(description = "revoked at")
    private LocalDateTime revokedAt;
}
