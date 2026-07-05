package com.telecom.scm.order.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AftersaleStatusEnum;
import com.telecom.scm.common.enums.OrderStatusEnum;
import com.telecom.scm.common.enums.PayStatusEnum;
import com.telecom.scm.common.enums.ShipmentStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 订单主表实体，映射 order_info 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_info")
public class OrderInfoEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "客户 ID")
    private Long customerId;

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "订单来源")
    private String orderSource;

    @Schema(description = "订单状态")
    private OrderStatusEnum orderStatus;

    @Schema(description = "支付状态")
    private PayStatusEnum payStatus;

    @Schema(description = "物流状态")
    private ShipmentStatusEnum shipmentStatus;

    @Schema(description = "售后状态")
    private AftersaleStatusEnum aftersaleStatus;

    @Schema(description = "商品金额")
    private BigDecimal goodsAmount;

    @Schema(description = "运费金额")
    private BigDecimal freightAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "使用积分")
    private Long usedPoints;

    @Schema(description = "积分抵扣金额")
    private BigDecimal pointsDeductionAmount;

    @Schema(description = "应付金额")
    private BigDecimal payAmount;

    @Schema(description = "成本金额")
    private BigDecimal costAmount;

    @Schema(description = "利润金额")
    private BigDecimal profitAmount;

    @Schema(description = "支付方式")
    private String payMethod;

    @Schema(description = "客户备注")
    private String customerRemark;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "收货省份")
    private String receiverProvince;

    @Schema(description = "收货城市")
    private String receiverCity;

    @Schema(description = "收货区县")
    private String receiverDistrict;

    @Schema(description = "详细地址")
    private String receiverAddress;
}
