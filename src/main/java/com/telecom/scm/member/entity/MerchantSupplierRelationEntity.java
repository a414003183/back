package com.telecom.scm.member.entity;

import java.io.Serial;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** MerchantSupplierRelationEntity 实体，映射 merchant_supplier_relation 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_supplier_relation")
public class MerchantSupplierRelationEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "relation id")
    private Long relationId;

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "供应商 ID")
    private Long supplierId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "cooperation start at")
    private LocalDateTime cooperationStartAt;

    @Schema(description = "cooperation end at")
    private LocalDateTime cooperationEndAt;
}
