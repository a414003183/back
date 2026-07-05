package com.telecom.scm.member.convert;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.member.dto.response.MerchantGoodsOptionsResponse;
import com.telecom.scm.member.dto.response.MerchantReportOverviewResponse;
import com.telecom.scm.member.mapper.row.BrandOptionRow;
import com.telecom.scm.member.mapper.row.CategoryOptionRow;
import com.telecom.scm.member.mapper.row.MerchantReportSummaryRow;

/** 商家领域对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MerchantConvert {

    MerchantConvert INSTANCE = Mappers.getMapper(MerchantConvert.class);

    /** 将品牌/类目选项聚合为商家商品选项响应。 */
    MerchantGoodsOptionsResponse toMerchantGoodsOptionsResponse(
            List<BrandOptionRow> brands, List<CategoryOptionRow> categories);

    /**
     * 将报表汇总行转换为商家报表概览响应。
     *
     * <p>净利润相关字段在 Service 中根据业务条件二次计算。
     */
    @Mapping(target = "grossSales", source = "grossSales", qualifiedByName = "toDecimal")
    @Mapping(target = "refundAmount", source = "refundAmount", qualifiedByName = "toDecimal")
    @Mapping(target = "grossProfit", source = "grossProfit", qualifiedByName = "toDecimal")
    @Mapping(target = "profitAdjustment", source = "profitReduction", qualifiedByName = "toDecimal")
    MerchantReportOverviewResponse toMerchantReportOverviewResponse(MerchantReportSummaryRow row);

    @Named("toDecimal")
    default BigDecimal toDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
