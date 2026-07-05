package com.telecom.scm.order.mapper;

import java.math.BigDecimal;

public class OrderItemAdjustLogRow {

    private String id;
    private String orderItemId;
    private String itemName;
    private Integer oldQuantity;
    private Integer newQuantity;
    private BigDecimal oldFinalUnitPrice;
    private BigDecimal newFinalUnitPrice;
    private BigDecimal oldFinalAmount;
    private BigDecimal newFinalAmount;
    private String operatorName;
    private String remark;
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(Integer oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }

    public BigDecimal getOldFinalUnitPrice() {
        return oldFinalUnitPrice;
    }

    public void setOldFinalUnitPrice(BigDecimal oldFinalUnitPrice) {
        this.oldFinalUnitPrice = oldFinalUnitPrice;
    }

    public BigDecimal getNewFinalUnitPrice() {
        return newFinalUnitPrice;
    }

    public void setNewFinalUnitPrice(BigDecimal newFinalUnitPrice) {
        this.newFinalUnitPrice = newFinalUnitPrice;
    }

    public BigDecimal getOldFinalAmount() {
        return oldFinalAmount;
    }

    public void setOldFinalAmount(BigDecimal oldFinalAmount) {
        this.oldFinalAmount = oldFinalAmount;
    }

    public BigDecimal getNewFinalAmount() {
        return newFinalAmount;
    }

    public void setNewFinalAmount(BigDecimal newFinalAmount) {
        this.newFinalAmount = newFinalAmount;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
