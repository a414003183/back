package com.telecom.scm.aftersale.mapper;

import java.math.BigDecimal;

public class AftersaleProcessRow {

    private Long id;
    private String aftersaleNo;
    private Long orderId;
    private String orderNo;
    private Long customerId;
    private Long merchantId;
    private String aftersaleType;
    private BigDecimal applyAmount;
    private BigDecimal approvedAmount;
    private BigDecimal orderPayAmount;
    private Integer orderUsedPoints;
    private String aftersaleStatus;
    private boolean needReturn;
    private String returnTrackingNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAftersaleNo() {
        return aftersaleNo;
    }

    public void setAftersaleNo(String aftersaleNo) {
        this.aftersaleNo = aftersaleNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getAftersaleType() {
        return aftersaleType;
    }

    public void setAftersaleType(String aftersaleType) {
        this.aftersaleType = aftersaleType;
    }

    public BigDecimal getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(BigDecimal applyAmount) {
        this.applyAmount = applyAmount;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public BigDecimal getOrderPayAmount() {
        return orderPayAmount;
    }

    public void setOrderPayAmount(BigDecimal orderPayAmount) {
        this.orderPayAmount = orderPayAmount;
    }

    public Integer getOrderUsedPoints() {
        return orderUsedPoints;
    }

    public void setOrderUsedPoints(Integer orderUsedPoints) {
        this.orderUsedPoints = orderUsedPoints;
    }

    public String getAftersaleStatus() {
        return aftersaleStatus;
    }

    public void setAftersaleStatus(String aftersaleStatus) {
        this.aftersaleStatus = aftersaleStatus;
    }

    public boolean isNeedReturn() {
        return needReturn;
    }

    public void setNeedReturn(boolean needReturn) {
        this.needReturn = needReturn;
    }

    public String getReturnTrackingNo() {
        return returnTrackingNo;
    }

    public void setReturnTrackingNo(String returnTrackingNo) {
        this.returnTrackingNo = returnTrackingNo;
    }
}
