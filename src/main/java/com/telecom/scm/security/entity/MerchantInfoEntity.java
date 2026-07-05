package com.telecom.scm.security.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 商家信息实体，映射 merchant_info 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_info")
public class MerchantInfoEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "会员 ID")
    private Long memberId;

    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系人电话")
    private String contactPhone;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;
}
