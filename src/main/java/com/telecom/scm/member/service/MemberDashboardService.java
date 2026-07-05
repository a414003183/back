package com.telecom.scm.member.service;

import java.util.List;

import com.telecom.scm.member.dto.response.MetricCardResponse;

public interface MemberDashboardService {

    List<MetricCardResponse> customerMetrics(String username);

    List<MetricCardResponse> merchantMetrics(String username);

    List<MetricCardResponse> supplierMetrics(String username);
}
