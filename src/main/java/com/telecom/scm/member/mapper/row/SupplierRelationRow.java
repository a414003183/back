package com.telecom.scm.member.mapper.row;

public class SupplierRelationRow {

    private String id;
    private Long merchantId;
    private String merchantName;
    private String contactName;
    private String contactPhone;
    private String merchantCode;
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
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
