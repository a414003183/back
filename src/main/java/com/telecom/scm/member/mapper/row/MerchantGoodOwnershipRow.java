package com.telecom.scm.member.mapper.row;

public class MerchantGoodOwnershipRow {

    private Long merchantGoodsId;
    private Long supplierId;
    private String sourceType;
    private String cooperationStatus;
    private String authorizationStatus;

    public Long getMerchantGoodsId() {
        return merchantGoodsId;
    }

    public void setMerchantGoodsId(Long merchantGoodsId) {
        this.merchantGoodsId = merchantGoodsId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCooperationStatus() {
        return cooperationStatus;
    }

    public void setCooperationStatus(String cooperationStatus) {
        this.cooperationStatus = cooperationStatus;
    }

    public String getAuthorizationStatus() {
        return authorizationStatus;
    }

    public void setAuthorizationStatus(String authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }
}
