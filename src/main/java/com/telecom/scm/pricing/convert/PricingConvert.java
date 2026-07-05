package com.telecom.scm.pricing.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.pricing.dto.response.PricingOptionsResponse;
import com.telecom.scm.pricing.mapper.row.BrandOptionRow;
import com.telecom.scm.pricing.mapper.row.CategoryOptionRow;
import com.telecom.scm.pricing.mapper.row.CustomerOptionRow;
import com.telecom.scm.pricing.mapper.row.MemberLevelOptionRow;
import com.telecom.scm.pricing.mapper.row.MerchantSkuOptionRow;

/**
 * 定价领域对象转换器。
 *
 * <p>使用 MapStruct 替代手写 getter/setter 转换，编译期生成代码，类型安全且性能接近手写。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PricingConvert {

    PricingConvert INSTANCE = Mappers.getMapper(PricingConvert.class);

    /** 将各类选项数据聚合为定价选项响应。 */
    PricingOptionsResponse toPricingOptionsResponse(
            List<BrandOptionRow> brands,
            List<CategoryOptionRow> categories,
            List<CustomerOptionRow> customers,
            List<MerchantSkuOptionRow> skus,
            List<MemberLevelOptionRow> memberLevels);
}
