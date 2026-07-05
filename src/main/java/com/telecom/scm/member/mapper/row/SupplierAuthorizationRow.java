package com.telecom.scm.member.mapper.row;

import java.math.BigDecimal;

public class SupplierAuthorizationRow {

    private String id;
    private Long merchantId;
    private String merchantName;
    private Long supplierSkuId;
    private String spuName;
    private String skuName;
    private String specText;
    private BigDecimal authorizedPrice;
    private Integer allocatedStockQty;
    private String authStatus;
    private String relationStatus;
    private String authorizedAt;
    private String revokedAt;
    private String remark;
    private String updatedAt;
    private String merchantGoodsId;
    private String importedSaleStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Long getSupplierSkuId() {
        return supplierSkuId;
    }

    public void setSupplierSkuId(Long supplierSkuId) {
        this.supplierSkuId = supplierSkuId;
    }

    public String getSpuName() {
        return spuName;
    }

    public void setSpuName(String spuName) {
        this.spuName = spuName;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSpecText() {
        return specText;
    }

    public void setSpecText(String specText) {
        this.specText = specText;
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

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public String getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(String relationStatus) {
        this.relationStatus = relationStatus;
    }

    public String getAuthorizedAt() {
        return authorizedAt;
    }

    public void setAuthorizedAt(String authorizedAt) {
        this.authorizedAt = authorizedAt;
    }

    public String getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(String revokedAt) {
        this.revokedAt = revokedAt;
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

    public String getMerchantGoodsId() {
        return merchantGoodsId;
    }

    public void setMerchantGoodsId(String merchantGoodsId) {
        this.merchantGoodsId = merchantGoodsId;
    }

    public String getImportedSaleStatus() {
        return importedSaleStatus;
    }

    public void setImportedSaleStatus(String importedSaleStatus) {
        this.importedSaleStatus = importedSaleStatus;
    }
}
