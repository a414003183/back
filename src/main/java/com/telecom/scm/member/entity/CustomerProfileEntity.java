package com.telecom.scm.member.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** CustomerProfileEntity 实体，映射 customer_profile 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_profile")
public class CustomerProfileEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "客户 ID")
    private Long customerId;

    @Schema(description = "company name")
    private String companyName;

    @Schema(description = "contact name")
    private String contactName;

    @Schema(description = "contact phone")
    private String contactPhone;

    @Schema(description = "invoice title")
    private String invoiceTitle;

    @Schema(description = "tax no")
    private String taxNo;

    @Schema(description = "bank name")
    private String bankName;

    @Schema(description = "bank account")
    private String bankAccount;
}
