package com.telecom.scm.points.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.points.convert.PointConvert;
import com.telecom.scm.points.dto.PointDeductionSnapshot;
import com.telecom.scm.points.entity.PointAccountEntity;
import com.telecom.scm.points.entity.PointRecordEntity;
import com.telecom.scm.points.entity.PointRuleEntity;
import com.telecom.scm.points.mapper.PointMapper;

@Service
public class PointLedgerServiceImpl implements PointLedgerService {

    private final PointMapper pointMapper;

    public PointLedgerServiceImpl(PointMapper pointMapper) {
        this.pointMapper = pointMapper;
    }

    @Override
    public Map<String, Object> customerPoints(String username) {
        Map<String, Object> context = requireCustomerContext(username);
        Long customerId = toLong(context.get("customerId"));
        PointAccountEntity account = ensurePointAccount(customerId);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("summary", account);
        data.put("deductionRule", deductionRuleSummary());
        data.put("records", pointMapper.selectPointRecordsByCustomerId(customerId));
        return data;
    }

    @Override
    public Map<String, Object> customerPointRecords(
            String username,
            String changeType,
            String startDate,
            String endDate,
            int page,
            int pageSize) {
        Map<String, Object> context = requireCustomerContext(username);
        Long customerId = toLong(context.get("customerId"));
        int offset = Math.max(0, (page - 1)) * pageSize;
        int limit = Math.max(1, Math.min(pageSize, 500));
        List<Map<String, Object>> rows =
                pointMapper.selectPointRecordsByCustomerIdPaged(
                        customerId, changeType, startDate, endDate, offset, limit);
        long total =
                pointMapper.countPointRecordsByCustomerId(
                        customerId, changeType, startDate, endDate);
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", row.get("id"));
            item.put("source", row.get("sourceType"));
            item.put("points", row.get("changePoints"));
            item.put("type", row.get("changeType"));
            item.put("createdAt", row.get("createdAt"));
            list.add(item);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", list);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", limit);
        return data;
    }

    @Override
    public Map<String, Object> customerReferral(String username) {
        Map<String, Object> context = requireCustomerContext(username);
        Long customerId = toLong(context.get("customerId"));
        Map<String, Object> summary = pointMapper.selectReferralSummary(customerId);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("summary", summary == null ? Map.of() : summary);
        data.put("referredCustomers", pointMapper.selectReferralRows(customerId));
        data.put("totalReferralBonusPoints", pointMapper.sumReferralBonusPoints(customerId));
        return data;
    }

    @Transactional
    @Override
    public void rewardOrderCompletion(
            Long orderId, Long customerId, BigDecimal payAmount, String orderNo) {
        if (pointMapper.countPointRecordsBySource(customerId, "ORDER", orderId) > 0) {
            return;
        }
        int points = calculatePoints("ORDER_REBATE", payAmount);
        if (points <= 0) {
            return;
        }
        changePoints(
                customerId, points, "INCREASE", "ORDER", orderId, "order finished: " + orderNo);
        rewardReferralBonus(orderId, customerId, payAmount, orderNo);
    }

    @Transactional
    @Override
    public void consumeOrderPoints(Long orderId, Long customerId, int usedPoints, String orderNo) {
        if (usedPoints <= 0) {
            return;
        }
        if (pointMapper.countPointRecordsBySource(customerId, "ORDER_DEDUCTION_USE", orderId) > 0) {
            return;
        }
        PointAccountEntity account = ensurePointAccount(customerId);
        int currentPoints = account.getCurrentPoints() == null ? 0 : account.getCurrentPoints();
        int points = Math.min(currentPoints, usedPoints);
        if (points <= 0) {
            return;
        }
        changePoints(
                customerId,
                points,
                "DECREASE",
                "ORDER_DEDUCTION_USE",
                orderId,
                "points deduction use: " + orderNo);
    }

    @Override
    public PointDeductionSnapshot resolveOrderDeduction(
            Long customerId, BigDecimal orderAmount, boolean usePoints) {
        if (!usePoints || orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return new PointDeductionSnapshot(0, BigDecimal.ZERO);
        }

        PointAccountEntity account = ensurePointAccount(customerId);
        int currentPoints = account.getCurrentPoints() == null ? 0 : account.getCurrentPoints();
        if (currentPoints <= 0) {
            return new PointDeductionSnapshot(0, BigDecimal.ZERO);
        }

        PointRuleEntity rule = pointMapper.selectPointRule("ORDER_DEDUCTION");
        if (!isDeductionEnabled(rule)) {
            return new PointDeductionSnapshot(0, BigDecimal.ZERO);
        }

        BigDecimal deductionRatio =
                rule.getDeductionRatio() == null ? BigDecimal.ZERO : rule.getDeductionRatio();
        BigDecimal maxDeductionRatio =
                rule.getMaxDeductionRatio() == null ? BigDecimal.ZERO : rule.getMaxDeductionRatio();
        if (deductionRatio.compareTo(BigDecimal.ZERO) <= 0) {
            return new PointDeductionSnapshot(0, BigDecimal.ZERO);
        }

        BigDecimal maxAllowedAmount =
                orderAmount
                        .multiply(maxDeductionRatio)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
        int maxAllowedPointsByOrder =
                orderAmount.divide(deductionRatio, 0, RoundingMode.DOWN).intValue();
        int maxAllowedPointsByRatio =
                maxAllowedAmount.divide(deductionRatio, 0, RoundingMode.DOWN).intValue();
        int usedPoints =
                Math.max(
                        0,
                        Math.min(
                                currentPoints,
                                Math.min(maxAllowedPointsByOrder, maxAllowedPointsByRatio)));
        if (usedPoints <= 0) {
            return new PointDeductionSnapshot(0, BigDecimal.ZERO);
        }

        BigDecimal deductionAmount =
                deductionRatio
                        .multiply(BigDecimal.valueOf(usedPoints))
                        .setScale(2, RoundingMode.DOWN);
        if (deductionAmount.compareTo(orderAmount) > 0) {
            deductionAmount = orderAmount;
        }
        return new PointDeductionSnapshot(usedPoints, deductionAmount);
    }

    @Transactional(rollbackFor = Exception.class)
    public void applyRefundPointChanges(
            Long aftersaleId,
            Long orderId,
            Long customerId,
            BigDecimal refundAmount,
            BigDecimal orderPayAmount,
            Integer orderUsedPoints,
            String orderNo) {
        reclaimOrderRewardPoints(aftersaleId, orderId, customerId, refundAmount, orderNo);
        returnUsedPointsForRefund(
                aftersaleId,
                orderId,
                customerId,
                refundAmount,
                orderPayAmount,
                orderUsedPoints,
                orderNo);
    }

    @Transactional
    protected void reclaimOrderRewardPoints(
            Long aftersaleId,
            Long orderId,
            Long customerId,
            BigDecimal refundAmount,
            String orderNo) {
        if (pointMapper.countPointRecordsBySource(customerId, "REFUND_RECLAIM", aftersaleId) > 0) {
            return;
        }
        int totalRewardPoints = toInt(pointMapper.sumOrderRewardPoints(customerId, orderId));
        if (totalRewardPoints <= 0) {
            deductReferralBonus(aftersaleId, customerId, refundAmount, orderNo);
            return;
        }

        int reclaimedPoints = toInt(pointMapper.sumRefundReclaimPointsByOrder(customerId, orderId));
        PointAccountEntity account = ensurePointAccount(customerId);
        int currentPoints = account.getCurrentPoints() == null ? 0 : account.getCurrentPoints();
        int remainingRewardPoints = Math.max(0, totalRewardPoints - reclaimedPoints);
        int targetPoints = calculatePoints("REFUND_DEDUCT", refundAmount);
        int points = Math.min(currentPoints, Math.min(remainingRewardPoints, targetPoints));
        if (points > 0) {
            changePoints(
                    customerId,
                    points,
                    "DECREASE",
                    "REFUND_RECLAIM",
                    aftersaleId,
                    "refund reclaim: " + orderNo);
        }
        deductReferralBonus(aftersaleId, customerId, refundAmount, orderNo);
    }

    @Transactional
    protected void returnUsedPointsForRefund(
            Long aftersaleId,
            Long orderId,
            Long customerId,
            BigDecimal refundAmount,
            BigDecimal orderPayAmount,
            Integer orderUsedPoints,
            String orderNo) {
        if (pointMapper.countPointRecordsBySource(customerId, "REFUND_RETURN", aftersaleId) > 0) {
            return;
        }

        int totalUsedPoints = orderUsedPoints == null ? 0 : orderUsedPoints;
        if (totalUsedPoints <= 0) {
            return;
        }

        int returnedPoints = toInt(pointMapper.sumRefundReturnPointsByOrder(customerId, orderId));
        int remainingPoints = Math.max(0, totalUsedPoints - returnedPoints);
        if (remainingPoints <= 0) {
            return;
        }

        int targetPoints;
        if (orderPayAmount == null || orderPayAmount.compareTo(BigDecimal.ZERO) <= 0) {
            targetPoints = remainingPoints;
        } else {
            targetPoints =
                    BigDecimal.valueOf(totalUsedPoints)
                            .multiply(refundAmount)
                            .divide(orderPayAmount, 0, RoundingMode.DOWN)
                            .intValue();
        }

        int points = Math.min(remainingPoints, targetPoints);
        if (points <= 0) {
            return;
        }
        changePoints(
                customerId,
                points,
                "INCREASE",
                "REFUND_RETURN",
                aftersaleId,
                "refund return used points: " + orderNo);
    }

    @Transactional
    protected void rewardReferralBonus(
            Long orderId, Long customerId, BigDecimal payAmount, String orderNo) {
        Long referrerCustomerId = pointMapper.selectReferrerCustomerId(customerId);
        if (referrerCustomerId == null) {
            return;
        }
        if (pointMapper.countPointRecordsBySource(referrerCustomerId, "REFERRAL", orderId) > 0) {
            return;
        }
        int points = calculatePoints("REFERRAL_BONUS", payAmount);
        if (points <= 0) {
            return;
        }
        changePoints(
                referrerCustomerId,
                points,
                "INCREASE",
                "REFERRAL",
                orderId,
                "referral bonus: " + orderNo);
    }

    @Transactional
    protected void deductReferralBonus(
            Long aftersaleId, Long customerId, BigDecimal refundAmount, String orderNo) {
        Long referrerCustomerId = pointMapper.selectReferrerCustomerId(customerId);
        if (referrerCustomerId == null) {
            return;
        }
        if (pointMapper.countPointRecordsBySource(
                        referrerCustomerId, "REFERRAL_RECLAIM", aftersaleId)
                > 0) {
            return;
        }
        PointAccountEntity account = ensurePointAccount(referrerCustomerId);
        int currentPoints = account.getCurrentPoints() == null ? 0 : account.getCurrentPoints();
        int points = Math.min(currentPoints, calculatePoints("REFERRAL_BONUS", refundAmount));
        if (points <= 0) {
            return;
        }
        changePoints(
                referrerCustomerId,
                points,
                "DECREASE",
                "REFERRAL_RECLAIM",
                aftersaleId,
                "referral bonus deduct: " + orderNo);
    }

    @Transactional
    protected void changePoints(
            Long customerId,
            int points,
            String changeType,
            String sourceType,
            Long sourceId,
            String remark) {
        PointAccountEntity account = ensurePointAccount(customerId);
        Long pointAccountId = account.getId();
        int currentPoints = account.getCurrentPoints() == null ? 0 : account.getCurrentPoints();
        int nextBalance;
        if ("INCREASE".equals(changeType)) {
            pointMapper.increasePoints(pointAccountId, points);
            nextBalance = currentPoints + points;
        } else {
            pointMapper.decreasePoints(pointAccountId, points);
            nextBalance = Math.max(0, currentPoints - points);
        }

        PointRecordEntity payload =
                PointConvert.INSTANCE.toPointRecordEntity(
                        pointAccountId,
                        customerId,
                        changeType,
                        sourceType,
                        sourceId,
                        points,
                        nextBalance,
                        remark);
        pointMapper.insertPointRecord(payload);
    }

    private PointAccountEntity ensurePointAccount(Long customerId) {
        PointAccountEntity account = pointMapper.selectPointAccountByCustomerId(customerId);
        if (account != null) {
            return account;
        }
        PointAccountEntity payload =
                PointConvert.INSTANCE.toPointAccountEntity(customerId, AccountStatusEnum.ENABLED);
        pointMapper.insertPointAccount(payload);
        PointAccountEntity created = pointMapper.selectPointAccountByCustomerId(customerId);
        if (created == null) {
            throw new BusinessException(500, "point account init failed");
        }
        return created;
    }

    private int calculatePoints(String ruleType, BigDecimal amount) {
        PointRuleEntity rule = pointMapper.selectPointRule(ruleType);
        if (rule == null || rule.getRuleValue() == null) {
            return 0;
        }
        BigDecimal ruleValue = rule.getRuleValue();
        String valueType = rule.getValueType();
        if ("FIXED".equalsIgnoreCase(valueType)) {
            return ruleValue.setScale(0, RoundingMode.DOWN).intValue();
        }
        return amount.multiply(ruleValue)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN)
                .intValue();
    }

    private Map<String, Object> requireCustomerContext(String username) {
        Map<String, Object> context = pointMapper.selectCustomerContextByUsername(username);
        if (context == null || context.isEmpty()) {
            throw new BusinessException(403, "customer account is unavailable");
        }
        return context;
    }

    private Map<String, Object> deductionRuleSummary() {
        PointRuleEntity rule = pointMapper.selectPointRule("ORDER_DEDUCTION");
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("enabled", isDeductionEnabled(rule));
        summary.put(
                "deductionRatio",
                rule == null || rule.getDeductionRatio() == null
                        ? BigDecimal.ZERO
                        : rule.getDeductionRatio());
        summary.put(
                "maxDeductionRatio",
                rule == null || rule.getMaxDeductionRatio() == null
                        ? BigDecimal.ZERO
                        : rule.getMaxDeductionRatio());
        return summary;
    }

    private boolean isDeductionEnabled(PointRuleEntity rule) {
        return rule != null && rule.getDeductionEnabled() != null && rule.getDeductionEnabled();
    }

    private Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private int toInt(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }
}
