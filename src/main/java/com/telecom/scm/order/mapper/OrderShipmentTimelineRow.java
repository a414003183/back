package com.telecom.scm.order.mapper;

public class OrderShipmentTimelineRow {

    private String id;
    private String carrierName;
    private String trackingNo;
    private String logisticsRemark;
    private String shipOperatorName;
    private String signOperatorName;
    private String shipTime;
    private String signTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLogisticsRemark() {
        return logisticsRemark;
    }

    public void setLogisticsRemark(String logisticsRemark) {
        this.logisticsRemark = logisticsRemark;
    }

    public String getShipOperatorName() {
        return shipOperatorName;
    }

    public void setShipOperatorName(String shipOperatorName) {
        this.shipOperatorName = shipOperatorName;
    }

    public String getSignOperatorName() {
        return signOperatorName;
    }

    public void setSignOperatorName(String signOperatorName) {
        this.signOperatorName = signOperatorName;
    }

    public String getShipTime() {
        return shipTime;
    }

    public void setShipTime(String shipTime) {
        this.shipTime = shipTime;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }
}
