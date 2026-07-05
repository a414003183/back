package com.telecom.scm.aftersale.mapper;

public class AftersaleRow {

    private String id;
    private String aftersaleNo;
    private String orderNo;
    private String customerName;
    private String merchantName;
    private String aftersaleType;
    private double applyAmount;
    private String aftersaleStatus;
    private boolean needReturn;
    private String returnTrackingNo;
    private String remark;
    private String createdAt;

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

    public String getAftersaleType() {
        return aftersaleType;
    }

    public void setAftersaleType(String aftersaleType) {
        this.aftersaleType = aftersaleType;
    }

    public double getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(double applyAmount) {
        this.applyAmount = applyAmount;
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
