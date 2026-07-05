package com.telecom.scm.pricing.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.admin.dto.request.SaveCustomerLevelConfigRequest;
import com.telecom.scm.audit.service.AuditLogService;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.pricing.entity.CustomerLevelConfigEntity;
import com.telecom.scm.pricing.mapper.CustomerLevelMapper;
import com.telecom.scm.pricing.mapper.row.CustomerGrowthInfoRow;
import com.telecom.scm.pricing.mapper.row.CustomerLevelConfigRow;
import com.telecom.scm.pricing.mapper.row.MemberLevelOptionRow;

@Service
public class CustomerLevelServiceImpl implements CustomerLevelService {

    private final CustomerLevelMapper customerLevelMapper;
    private final AuditLogService auditLogService;

    public CustomerLevelServiceImpl(
            CustomerLevelMapper customerLevelMapper, AuditLogService auditLogService) {
        this.customerLevelMapper = customerLevelMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    public PageResult<CustomerLevelConfigRow> configs(int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<CustomerLevelConfigRow> list =
                customerLevelMapper.selectCustomerLevelConfigs(offset, pageSize);
        long total = customerLevelMapper.countCustomerLevelConfigs();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerLevelConfigRow saveConfig(
            Long operatorId, SaveCustomerLevelConfigRequest request) {
        CustomerLevelConfigEntity param = new CustomerLevelConfigEntity();
        param.setLevelCode(normalizeLevelCode(request.levelCode()));
        param.setLevelName(request.levelName().trim());
        param.setUpgradeThresholdAmount(request.upgradeThresholdAmount());
        param.setSortNo(request.sortNo() == null ? 0 : request.sortNo());
        param.setStatus(request.status() != null ? request.status() : AccountStatusEnum.ENABLED);
        param.setRemark(normalizeText(request.remark()));
        param.setOperatorId(operatorId);
        customerLevelMapper.upsertCustomerLevelConfig(param);
        CustomerLevelConfigRow saved =
                customerLevelMapper.selectCustomerLevelConfigByLevelCode(
                        normalizeLevelCode(request.levelCode()));
        if (saved != null) {
            return saved;
        }
        CustomerLevelConfigRow fallback = new CustomerLevelConfigRow();
        fallback.setLevelCode(normalizeLevelCode(request.levelCode()));
        fallback.setLevelName(request.levelName().trim());
        return fallback;
    }

    @Override
    public List<MemberLevelOptionRow> levelOptions() {
        return customerLevelMapper.selectEnabledCustomerLevelOptions();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerLevelConfigRow updateStatus(String levelCode, AccountStatusEnum status) {
        CustomerLevelConfigEntity param = new CustomerLevelConfigEntity();
        param.setLevelCode(normalizeLevelCode(levelCode));
        param.setStatus(status);
        customerLevelMapper.updateCustomerLevelStatus(param);
        CustomerLevelConfigRow updated =
                customerLevelMapper.selectCustomerLevelConfigByLevelCode(
                        normalizeLevelCode(levelCode));
        if (updated != null) {
            return updated;
        }
        CustomerLevelConfigRow fallback = new CustomerLevelConfigRow();
        fallback.setLevelCode(normalizeLevelCode(levelCode));
        fallback.setStatus(status.getCode());
        return fallback;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshCustomerLevelOnOrderFinished(
            Long customerId, Long operatorId, String operatorName, String orderNo) {
        CustomerGrowthInfoRow current = customerLevelMapper.selectCustomerGrowthInfo(customerId);
        if (current == null) {
            return;
        }

        BigDecimal accumulatedPaidAmount =
                customerLevelMapper.selectFinishedPaidAmountByCustomerId(customerId);
        if (accumulatedPaidAmount == null) {
            accumulatedPaidAmount = BigDecimal.ZERO;
        }

        List<MemberLevelOptionRow> enabledLevels = levelOptions();
        Map<String, Integer> ranks = new HashMap<>();
        String matchedLevelCode = textValue(current.getMemberLevel());
        for (int index = 0; index < enabledLevels.size(); index++) {
            MemberLevelOptionRow config = enabledLevels.get(index);
            String levelCode = textValue(config.getValue());
            ranks.put(levelCode, index);
            BigDecimal threshold = config.getUpgradeThresholdAmount();
            if (threshold == null) {
                threshold = BigDecimal.ZERO;
            }
            if (accumulatedPaidAmount.compareTo(threshold) >= 0) {
                matchedLevelCode = levelCode;
            }
        }

        String currentLevelCode = textValue(current.getMemberLevel());
        int currentRank = ranks.getOrDefault(currentLevelCode, -1);
        int matchedRank = ranks.getOrDefault(matchedLevelCode, currentRank);
        boolean upgraded = matchedRank > currentRank;
        String finalLevelCode = upgraded ? matchedLevelCode : currentLevelCode;

        customerLevelMapper.updateCustomerGrowth(
                customerId,
                accumulatedPaidAmount,
                finalLevelCode,
                upgraded ? LocalDateTime.now() : null,
                operatorId);

        if (upgraded) {
            auditLogService.recordOperation(
                    operatorId,
                    operatorName,
                    "CustomerLevel",
                    "AUTO_LEVEL_UPGRADE",
                    "/internal/customer-level/upgrade",
                    "SYSTEM",
                    "{\"customerId\":" + customerId + ",\"orderNo\":\"" + orderNo + "\"}",
                    "SUCCESS",
                    "customer upgraded from " + currentLevelCode + " to " + finalLevelCode,
                    null);
        }
    }

    private String normalizeLevelCode(String levelCode) {
        return levelCode == null ? "" : levelCode.trim().toUpperCase();
    }

    private String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String textValue(String value) {
        return value == null ? "" : value;
    }
}
