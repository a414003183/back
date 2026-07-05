package com.telecom.scm.pricing.mapper.row;

import java.math.BigDecimal;

public class MemberLevelOptionRow {

    private String value;
    private String label;
    private BigDecimal upgradeThresholdAmount;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getUpgradeThresholdAmount() {
        return upgradeThresholdAmount;
    }

    public void setUpgradeThresholdAmount(BigDecimal upgradeThresholdAmount) {
        this.upgradeThresholdAmount = upgradeThresholdAmount;
    }
}
