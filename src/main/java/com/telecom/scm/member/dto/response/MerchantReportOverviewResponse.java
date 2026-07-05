package com.telecom.scm.member.dto.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "商家报表概览响应")
public class MerchantReportOverviewResponse {

    @Schema(description = "Orders")
    private Long totalOrders;

    @Schema(description = "Sales")
    private BigDecimal grossSales;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "Sales")
    private BigDecimal netSales;

    @Schema(description = "Profit")
    private BigDecimal grossProfit;

    @Schema(description = "Adjustment")
    private BigDecimal profitAdjustment;

    @Schema(description = "Profit")
    private BigDecimal netProfit;

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

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getNetSales() {
        return netSales;
    }

    public void setNetSales(BigDecimal netSales) {
        this.netSales = netSales;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getProfitAdjustment() {
        return profitAdjustment;
    }

    public void setProfitAdjustment(BigDecimal profitAdjustment) {
        this.profitAdjustment = profitAdjustment;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }
}
