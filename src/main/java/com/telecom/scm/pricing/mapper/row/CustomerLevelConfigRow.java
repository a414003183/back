package com.telecom.scm.pricing.mapper.row;

import java.math.BigDecimal;

public class CustomerLevelConfigRow {

    private String id;
    private String levelCode;
    private String levelName;
    private BigDecimal upgradeThresholdAmount;
    private Integer sortNo;
    private String status;
    private String remark;
    private String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public BigDecimal getUpgradeThresholdAmount() {
        return upgradeThresholdAmount;
    }

    public void setUpgradeThresholdAmount(BigDecimal upgradeThresholdAmount) {
        this.upgradeThresholdAmount = upgradeThresholdAmount;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
