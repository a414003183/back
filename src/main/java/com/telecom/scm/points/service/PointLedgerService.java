package com.telecom.scm.points.service;

import java.math.BigDecimal;
import java.util.Map;

import com.telecom.scm.points.dto.PointDeductionSnapshot;

public interface PointLedgerService {
    Map<String, Object> customerPoints(String username);

    Map<String, Object> customerPointRecords(
            String username,
            String changeType,
            String startDate,
            String endDate,
            int page,
            int pageSize);

    Map<String, Object> customerReferral(String username);

    void rewardOrderCompletion(Long orderId, Long customerId, BigDecimal payAmount, String orderNo);

    void consumeOrderPoints(Long orderId, Long customerId, int usedPoints, String orderNo);

    PointDeductionSnapshot resolveOrderDeduction(
            Long customerId, BigDecimal orderAmount, boolean usePoints);

    void applyRefundPointChanges(
            Long aftersaleId,
            Long orderId,
            Long customerId,
            BigDecimal refundAmount,
            BigDecimal orderPayAmount,
            Integer orderUsedPoints,
            String orderNo);
}
