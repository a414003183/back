package com.telecom.scm.security.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 供应商信息实体，映射 supplier_info 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("supplier_info")
public class SupplierInfoEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "会员 ID")
    private Long memberId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系人电话")
    private String contactPhone;

    @Schema(description = "状态")
    private String status;
}
