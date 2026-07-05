package com.telecom.scm.member.dto.response;

import java.util.List;

import com.telecom.scm.member.mapper.row.BrandOptionRow;
import com.telecom.scm.member.mapper.row.CategoryOptionRow;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "供应商产品Options响应")
public record SupplierProductOptionsResponse(
        @Schema(description = "brands") List<BrandOptionRow> brands,
        @Schema(description = "分类列表") List<CategoryOptionRow> categories) {}
