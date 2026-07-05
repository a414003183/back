package com.telecom.scm.order.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 订单状态变更日志实体，用于写入 order_status_log 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_status_log")
public class OrderStatusLogEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "old status")
    private String oldStatus;

    @Schema(description = "new status")
    private String newStatus;

    @Schema(description = "operation type")
    private String operationType;

    @Schema(description = "operator id")
    private Long operatorId;

    @Schema(description = "operator name")
    private String operatorName;
}
