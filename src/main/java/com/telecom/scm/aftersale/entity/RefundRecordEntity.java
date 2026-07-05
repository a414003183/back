package com.telecom.scm.aftersale.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 退款记录实体，对应 payment_record 表售后退款场景。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("refund_record")
public class RefundRecordEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "售后单 ID")
    private Long aftersaleId;

    @Schema(description = "支付方式")
    private String payMethod;

    @Schema(description = "应付金额")
    private BigDecimal payAmount;

    @Schema(description = "voucher file id")
    private Long voucherFileId;

    @Schema(description = "transaction no")
    private String transactionNo;

    @Schema(description = "confirmed by")
    private Long confirmedBy;
}
