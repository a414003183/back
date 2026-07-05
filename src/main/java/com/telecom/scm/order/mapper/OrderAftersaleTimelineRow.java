package com.telecom.scm.order.mapper;

import java.math.BigDecimal;

public class OrderAftersaleTimelineRow {

    private String id;
    private String aftersaleNo;
    private String aftersaleType;
    private String aftersaleStatus;
    private BigDecimal applyAmount;
    private BigDecimal approvedAmount;
    private String returnTrackingNo;
    private String createdAt;
    private String merchantReceiveTime;
    private String finishTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAftersaleNo() {
        return aftersaleNo;
    }

    public void setAftersaleNo(String aftersaleNo) {
        this.aftersaleNo = aftersaleNo;
    }

    public String getAftersaleType() {
        return aftersaleType;
    }

    public void setAftersaleType(String aftersaleType) {
        this.aftersaleType = aftersaleType;
    }

    public String getAftersaleStatus() {
        return aftersaleStatus;
    }

    public void setAftersaleStatus(String aftersaleStatus) {
        this.aftersaleStatus = aftersaleStatus;
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

    public String getReturnTrackingNo() {
        return returnTrackingNo;
    }

    public void setReturnTrackingNo(String returnTrackingNo) {
        this.returnTrackingNo = returnTrackingNo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMerchantReceiveTime() {
        return merchantReceiveTime;
    }

    public void setMerchantReceiveTime(String merchantReceiveTime) {
        this.merchantReceiveTime = merchantReceiveTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }
}
