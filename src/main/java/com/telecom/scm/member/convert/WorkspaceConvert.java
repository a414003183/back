package com.telecom.scm.member.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.member.dto.response.MerchantReportOverviewResponse;
import com.telecom.scm.member.dto.response.MerchantReportResponse;
import com.telecom.scm.member.mapper.row.MerchantCustomerRankingRow;
import com.telecom.scm.member.mapper.row.MerchantProductRankingRow;

/** 会员工作台领域对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkspaceConvert {

    WorkspaceConvert INSTANCE = Mappers.getMapper(WorkspaceConvert.class);

    /** 将报表概览与排名数据聚合为商家报表响应。 */
    MerchantReportResponse toMerchantReportResponse(
            MerchantReportOverviewResponse overview,
            List<MerchantCustomerRankingRow> customerRanking,
            List<MerchantProductRankingRow> productRanking);
}
