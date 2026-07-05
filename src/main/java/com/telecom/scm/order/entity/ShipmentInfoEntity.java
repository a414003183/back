package com.telecom.scm.order.entity;

import java.io.Serial;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 订单物流信息实体，对应 shipment_info 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shipment_info")
public class ShipmentInfoEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "ship status")
    private String shipStatus;

    @Schema(description = "carrier name")
    private String carrierName;

    @Schema(description = "tracking no")
    private String trackingNo;

    @Schema(description = "proof file id")
    private Long proofFileId;

    @Schema(description = "ship time")
    private LocalDateTime shipTime;

    @Schema(description = "logistics remark")
    private String logisticsRemark;
}
