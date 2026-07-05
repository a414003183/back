package com.telecom.scm.pricing.mapper.row;

import java.math.BigDecimal;

public class CustomerGrowthInfoRow {

    private String memberLevel;
    private BigDecimal accumulatedPaidAmount;
    private String lastLevelUpgradeAt;

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public BigDecimal getAccumulatedPaidAmount() {
        return accumulatedPaidAmount;
    }

    public void setAccumulatedPaidAmount(BigDecimal accumulatedPaidAmount) {
        this.accumulatedPaidAmount = accumulatedPaidAmount;
    }

    public String getLastLevelUpgradeAt() {
        return lastLevelUpgradeAt;
    }

    public void setLastLevelUpgradeAt(String lastLevelUpgradeAt) {
        this.lastLevelUpgradeAt = lastLevelUpgradeAt;
    }
}
