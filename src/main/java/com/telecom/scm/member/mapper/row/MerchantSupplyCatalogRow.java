package com.telecom.scm.member.mapper.row;

import java.math.BigDecimal;

public class MerchantSupplyCatalogRow {

    private Long supplierId;
    private String supplierName;
    private String supplierCode;
    private Long supplierSkuId;
    private String spuName;
    private String skuName;
    private String specText;
    private Long mainImageId;
    private Integer stockQty;
    private BigDecimal basePrice;
    private String relationStatus;
    private String authStatus;
    private BigDecimal authorizedPrice;
    private Integer allocatedStockQty;
    private String merchantGoodsId;
    private String importedSaleStatus;
    private String updatedAt;
    private String remark;

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
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

    public Long getMainImageId() {
        return mainImageId;
    }

    public void setMainImageId(Long mainImageId) {
        this.mainImageId = mainImageId;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
