package com.telecom.scm.member.dto.response;

import java.util.List;

import com.telecom.scm.member.mapper.row.MerchantOptionRow;
import com.telecom.scm.member.mapper.row.SupplierSkuOptionRow;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "供应商Options响应")
public record SupplierOptionsResponse(
        @Schema(description = "商家列表") List<MerchantOptionRow> merchants,
        @Schema(description = "产品列表") List<SupplierSkuOptionRow> products) {}
