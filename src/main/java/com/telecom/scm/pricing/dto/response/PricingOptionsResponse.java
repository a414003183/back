package com.telecom.scm.pricing.dto.response;

import java.util.List;

import com.telecom.scm.pricing.mapper.row.BrandOptionRow;
import com.telecom.scm.pricing.mapper.row.CategoryOptionRow;
import com.telecom.scm.pricing.mapper.row.CustomerOptionRow;
import com.telecom.scm.pricing.mapper.row.MemberLevelOptionRow;
import com.telecom.scm.pricing.mapper.row.MerchantSkuOptionRow;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "定价Options响应")
public class PricingOptionsResponse {

    @Schema(description = "brands")
    private List<BrandOptionRow> brands;

    @Schema(description = "分类列表")
    private List<CategoryOptionRow> categories;

    @Schema(description = "客户列表")
    private List<CustomerOptionRow> customers;

    @Schema(description = "SKU 列表")
    private List<MerchantSkuOptionRow> skus;

    @Schema(description = "Levels")
    private List<MemberLevelOptionRow> memberLevels;

    public List<BrandOptionRow> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandOptionRow> brands) {
        this.brands = brands;
    }

    public List<CategoryOptionRow> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryOptionRow> categories) {
        this.categories = categories;
    }

    public List<CustomerOptionRow> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerOptionRow> customers) {
        this.customers = customers;
    }

    public List<MerchantSkuOptionRow> getSkus() {
        return skus;
    }

    public void setSkus(List<MerchantSkuOptionRow> skus) {
        this.skus = skus;
    }

    public List<MemberLevelOptionRow> getMemberLevels() {
        return memberLevels;
    }

    public void setMemberLevels(List<MemberLevelOptionRow> memberLevels) {
        this.memberLevels = memberLevels;
    }
}
