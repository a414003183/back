package com.telecom.scm.member.mapper.row;

public class MerchantShipmentRow {

    private String id;
    private String orderNo;
    private String customerName;
    private String carrierName;
    private String trackingNo;
    private String shipStatus;
    private String shipTime;
    private String logisticsRemark;

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

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getShipStatus() {
        return shipStatus;
    }

    public void setShipStatus(String shipStatus) {
        this.shipStatus = shipStatus;
    }

    public String getShipTime() {
        return shipTime;
    }

    public void setShipTime(String shipTime) {
        this.shipTime = shipTime;
    }

    public String getLogisticsRemark() {
        return logisticsRemark;
    }

    public void setLogisticsRemark(String logisticsRemark) {
        this.logisticsRemark = logisticsRemark;
    }
}
