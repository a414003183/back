package com.telecom.scm.member.mapper.row;

public class MerchantSupplyRelationRow {

    private String id;
    private Long supplierId;
    private String supplierName;
    private String supplierCode;
    private String status;
    private String cooperationStartAt;
    private String cooperationEndAt;
    private String remark;
    private String updatedAt;
    private Long authorizedSkuCount;
    private Long importedSkuCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCooperationStartAt() {
        return cooperationStartAt;
    }

    public void setCooperationStartAt(String cooperationStartAt) {
        this.cooperationStartAt = cooperationStartAt;
    }

    public String getCooperationEndAt() {
        return cooperationEndAt;
    }

    public void setCooperationEndAt(String cooperationEndAt) {
        this.cooperationEndAt = cooperationEndAt;
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

    public Long getAuthorizedSkuCount() {
        return authorizedSkuCount;
    }

    public void setAuthorizedSkuCount(Long authorizedSkuCount) {
        this.authorizedSkuCount = authorizedSkuCount;
    }

    public Long getImportedSkuCount() {
        return importedSkuCount;
    }

    public void setImportedSkuCount(Long importedSkuCount) {
        this.importedSkuCount = importedSkuCount;
    }
}
