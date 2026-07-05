package com.telecom.scm.member.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** SupplierProfileEntity 实体，映射 supplier_profile 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("supplier_profile")
public class SupplierProfileEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "供应描述")
    private String supplyDesc;

    @Schema(description = "资质文件ID")
    private Long qualificationFileId;
}
