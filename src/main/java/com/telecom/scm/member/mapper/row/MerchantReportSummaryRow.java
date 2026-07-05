package com.telecom.scm.member.mapper.row;

import java.math.BigDecimal;

public class MerchantReportSummaryRow {

    private Long totalOrders;
    private BigDecimal grossSales;
    private BigDecimal grossProfit;
    private BigDecimal refundAmount;
    private BigDecimal profitReduction;

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getGrossSales() {
        return grossSales;
    }

    public void setGrossSales(BigDecimal grossSales) {
        this.grossSales = grossSales;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getProfitReduction() {
        return profitReduction;
    }

    public void setProfitReduction(BigDecimal profitReduction) {
        this.profitReduction = profitReduction;
    }
}
