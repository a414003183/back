package com.telecom.scm.member.mapper.row;

import java.math.BigDecimal;

public class CustomerContextRow {

    private Long userId;
    private Long memberId;
    private Long customerId;
    private String memberLevel;
    private BigDecimal accumulatedPaidAmount;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

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
}
