package com.telecom.scm.member.mapper.row;

public class SupplierProfileRow {

    private Long supplierId;
    private String supplierName;
    private String contactName;
    private String contactPhone;
    private String supplyDesc;
    private Long qualificationFileId;
    private String qualificationFileName;

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

    public String getSupplyDesc() {
        return supplyDesc;
    }

    public void setSupplyDesc(String supplyDesc) {
        this.supplyDesc = supplyDesc;
    }

    public Long getQualificationFileId() {
        return qualificationFileId;
    }

    public void setQualificationFileId(Long qualificationFileId) {
        this.qualificationFileId = qualificationFileId;
    }

    public String getQualificationFileName() {
        return qualificationFileName;
    }

    public void setQualificationFileName(String qualificationFileName) {
        this.qualificationFileName = qualificationFileName;
    }
}
