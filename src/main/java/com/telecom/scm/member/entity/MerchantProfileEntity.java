package com.telecom.scm.member.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** MerchantProfileEntity 实体，映射 merchant_profile 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_profile")
public class MerchantProfileEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "shop name")
    private String shopName;

    @Schema(description = "license no")
    private String licenseNo;

    @Schema(description = "contact name")
    private String contactName;

    @Schema(description = "contact phone")
    private String contactPhone;

    @Schema(description = "shop desc")
    private String shopDesc;

    @Schema(description = "状态")
    private AccountStatusEnum status;
}
