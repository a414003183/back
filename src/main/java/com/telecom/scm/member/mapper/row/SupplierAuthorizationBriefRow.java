package com.telecom.scm.member.mapper.row;

import java.math.BigDecimal;

public class SupplierAuthorizationBriefRow {

    private Long id;
    private Long supplierId;
    private Long merchantId;
    private Long supplierSkuId;
    private String authStatus;
    private BigDecimal authorizedPrice;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public BigDecimal getAuthorizedPrice() {
        return authorizedPrice;
    }

    public void setAuthorizedPrice(BigDecimal authorizedPrice) {
        this.authorizedPrice = authorizedPrice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
