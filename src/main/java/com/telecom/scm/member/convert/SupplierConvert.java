package com.telecom.scm.member.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.member.dto.response.SupplierOptionsResponse;
import com.telecom.scm.member.dto.response.SupplierProductOptionsResponse;
import com.telecom.scm.member.mapper.row.BrandOptionRow;
import com.telecom.scm.member.mapper.row.CategoryOptionRow;
import com.telecom.scm.member.mapper.row.MerchantOptionRow;
import com.telecom.scm.member.mapper.row.SupplierSkuOptionRow;

/** 供应商领域对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierConvert {

    SupplierConvert INSTANCE = Mappers.getMapper(SupplierConvert.class);

    /** 将品牌/类目选项聚合为供应商商品选项响应。 */
    SupplierProductOptionsResponse toSupplierProductOptionsResponse(
            List<BrandOptionRow> brands, List<CategoryOptionRow> categories);

    /** 将合作商家与自有 SKU 选项聚合为供应商选项响应。 */
    SupplierOptionsResponse toSupplierOptionsResponse(
            List<MerchantOptionRow> merchants, List<SupplierSkuOptionRow> products);
}
