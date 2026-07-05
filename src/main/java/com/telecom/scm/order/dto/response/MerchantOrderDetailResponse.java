package com.telecom.scm.order.dto.response;

import java.util.List;

import com.telecom.scm.order.mapper.MerchantOrderDetailItemRow;
import com.telecom.scm.order.mapper.MerchantOrderDetailRow;
import com.telecom.scm.order.mapper.OrderItemAdjustLogRow;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商家订单详情响应")
public class MerchantOrderDetailResponse {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "Name")
    private String customerName;

    @Schema(description = "Name")
    private String merchantName;

    @Schema(description = "Status")
    private String orderStatus;

    @Schema(description = "Status")
    private String payStatus;

    @Schema(description = "Status")
    private String shipmentStatus;

    @Schema(description = "Status")
    private String aftersaleStatus;

    @Schema(description = "Amount")
    private java.math.BigDecimal goodsAmount;

    @Schema(description = "Amount")
    private java.math.BigDecimal freightAmount;

    @Schema(description = "折扣金额")
    private java.math.BigDecimal discountAmount;

    @Schema(description = "支付金额")
    private java.math.BigDecimal payAmount;

    @Schema(description = "Amount")
    private java.math.BigDecimal costAmount;

    @Schema(description = "Amount")
    private java.math.BigDecimal profitAmount;

    @Schema(description = "收件人姓名")
    private String receiverName;

    @Schema(description = "收件人电话")
    private String receiverPhone;

    @Schema(description = "收件人地址")
    private String receiverAddress;

    @Schema(description = "Remark")
    private String customerRemark;

    @Schema(description = "At")
    private String createdAt;

    @Schema(description = "项列表")
    private List<MerchantOrderDetailItemRow> items;

    @Schema(description = "Logs")
    private List<OrderItemAdjustLogRow> adjustLogs;

    public MerchantOrderDetailResponse() {}

    public MerchantOrderDetailResponse(
            MerchantOrderDetailRow row,
            List<MerchantOrderDetailItemRow> items,
            List<OrderItemAdjustLogRow> adjustLogs) {
        this.id = row.getId();
        this.orderNo = row.getOrderNo();
        this.customerName = row.getCustomerName();
        this.merchantName = row.getMerchantName();
        this.orderStatus = row.getOrderStatus();
        this.payStatus = row.getPayStatus();
        this.shipmentStatus = row.getShipmentStatus();
        this.aftersaleStatus = row.getAftersaleStatus();
        this.goodsAmount = row.getGoodsAmount();
        this.freightAmount = row.getFreightAmount();
        this.discountAmount = row.getDiscountAmount();
        this.payAmount = row.getPayAmount();
        this.costAmount = row.getCostAmount();
        this.profitAmount = row.getProfitAmount();
        this.receiverName = row.getReceiverName();
        this.receiverPhone = row.getReceiverPhone();
        this.receiverAddress = row.getReceiverAddress();
        this.customerRemark = row.getCustomerRemark();
        this.createdAt = row.getCreatedAt();
        this.items = items;
        this.adjustLogs = adjustLogs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getAftersaleStatus() {
        return aftersaleStatus;
    }

    public void setAftersaleStatus(String aftersaleStatus) {
        this.aftersaleStatus = aftersaleStatus;
    }

    public java.math.BigDecimal getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(java.math.BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public java.math.BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(java.math.BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    public java.math.BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(java.math.BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public java.math.BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(java.math.BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public java.math.BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(java.math.BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public java.math.BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(java.math.BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getCustomerRemark() {
        return customerRemark;
    }

    public void setCustomerRemark(String customerRemark) {
        this.customerRemark = customerRemark;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<MerchantOrderDetailItemRow> getItems() {
        return items;
    }

    public void setItems(List<MerchantOrderDetailItemRow> items) {
        this.items = items;
    }

    public List<OrderItemAdjustLogRow> getAdjustLogs() {
        return adjustLogs;
    }

    public void setAdjustLogs(List<OrderItemAdjustLogRow> adjustLogs) {
        this.adjustLogs = adjustLogs;
    }
}
