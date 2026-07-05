package com.telecom.scm.member.mapper.row;

import java.math.BigDecimal;

public class MerchantImportAuthorizationRow {

    private Long supplierId;
    private Long merchantId;
    private Long supplierSkuId;
    private BigDecimal authorizedPrice;
    private Integer allocatedStockQty;
    private String relationStatus;
    private String authStatus;
    private BigDecimal basePrice;

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getSupplierSkuId() {
        return supplierSkuId;
    }

    public void setSupplierSkuId(Long supplierSkuId) {
        this.supplierSkuId = supplierSkuId;
    }

    public BigDecimal getAuthorizedPrice() {
        return authorizedPrice;
    }

    public void setAuthorizedPrice(BigDecimal authorizedPrice) {
        this.authorizedPrice = authorizedPrice;
    }

    public Integer getAllocatedStockQty() {
        return allocatedStockQty;
    }

    public void setAllocatedStockQty(Integer allocatedStockQty) {
        this.allocatedStockQty = allocatedStockQty;
    }

    public String getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(String relationStatus) {
        this.relationStatus = relationStatus;
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
}
