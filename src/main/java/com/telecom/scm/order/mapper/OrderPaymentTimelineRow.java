package com.telecom.scm.order.mapper;

import java.math.BigDecimal;

public class OrderPaymentTimelineRow {

    private String id;
    private String recordType;
    private String payMethod;
    private BigDecimal payAmount;
    private String voucherFileId;
    private String transactionNo;
    private String payStatus;
    private String aftersaleId;
    private String remark;
    private String createdAt;
    private String confirmedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getVoucherFileId() {
        return voucherFileId;
    }

    public void setVoucherFileId(String voucherFileId) {
        this.voucherFileId = voucherFileId;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getAftersaleId() {
        return aftersaleId;
    }

    public void setAftersaleId(String aftersaleId) {
        this.aftersaleId = aftersaleId;
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

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(String confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
