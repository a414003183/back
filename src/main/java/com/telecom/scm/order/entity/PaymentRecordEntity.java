package com.telecom.scm.order.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 支付记录实体，对应 payment_record 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_record")
public class PaymentRecordEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "售后单 ID")
    private Long aftersaleId;

    @Schema(description = "record type")
    private String recordType;

    @Schema(description = "支付方式")
    private String payMethod;

    @Schema(description = "应付金额")
    private BigDecimal payAmount;

    @Schema(description = "voucher file id")
    private Long voucherFileId;

    @Schema(description = "transaction no")
    private String transactionNo;

    @Schema(description = "支付状态")
    private String payStatus;

    @Schema(description = "confirmed by")
    private Long confirmedBy;
}
