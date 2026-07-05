package com.telecom.scm.order.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 订单商品调价日志实体，对应 order_item_adjust_log 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_item_adjust_log")
public class OrderItemAdjustLogEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "order item id")
    private Long orderItemId;

    @Schema(description = "old quantity")
    private Integer oldQuantity;

    @Schema(description = "new quantity")
    private Integer newQuantity;

    @Schema(description = "old final unit price")
    private BigDecimal oldFinalUnitPrice;

    @Schema(description = "new final unit price")
    private BigDecimal newFinalUnitPrice;

    @Schema(description = "old final amount")
    private BigDecimal oldFinalAmount;

    @Schema(description = "new final amount")
    private BigDecimal newFinalAmount;

    @Schema(description = "operator id")
    private Long operatorId;

    @Schema(description = "operator name")
    private String operatorName;
}
