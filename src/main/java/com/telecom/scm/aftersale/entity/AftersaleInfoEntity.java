package com.telecom.scm.aftersale.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AftersaleStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 售后主表实体，对应 aftersale_info 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("aftersale_info")
public class AftersaleInfoEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "售后单号")
    private String aftersaleNo;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "客户 ID")
    private Long customerId;

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "aftersale type")
    private String aftersaleType;

    @Schema(description = "reason type")
    private String reasonType;

    @Schema(description = "reason desc")
    private String reasonDesc;

    @Schema(description = "apply amount")
    private BigDecimal applyAmount;

    @Schema(description = "approved amount")
    private BigDecimal approvedAmount;

    @Schema(description = "售后状态")
    private AftersaleStatusEnum aftersaleStatus;

    @Schema(description = "need return")
    private Boolean needReturn;

    @Schema(description = "return tracking no")
    private String returnTrackingNo;
}
