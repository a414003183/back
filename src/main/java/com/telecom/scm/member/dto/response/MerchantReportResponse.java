package com.telecom.scm.member.dto.response;

import java.util.List;

import com.telecom.scm.member.mapper.row.MerchantCustomerRankingRow;
import com.telecom.scm.member.mapper.row.MerchantProductRankingRow;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商家报表响应")
public record MerchantReportResponse(
        @Schema(description = "概览") MerchantReportOverviewResponse overview,
        @Schema(description = "Ranking") List<MerchantCustomerRankingRow> customerRanking,
        @Schema(description = "Ranking") List<MerchantProductRankingRow> productRanking) {}
