package com.telecom.scm.admin.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.admin.convert.AdminConvert;
import com.telecom.scm.admin.dto.response.RegistrationApplicationResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.common.enums.MemberTypeEnum;
import com.telecom.scm.common.enums.RegistrationStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.security.mapper.IdentityManagementMapper;

@Service
public class AdminRegistrationServiceImpl implements AdminRegistrationService {

    private final IdentityManagementMapper identityManagementMapper;

    public AdminRegistrationServiceImpl(IdentityManagementMapper identityManagementMapper) {
        this.identityManagementMapper = identityManagementMapper;
    }

    @Override
    public PageResult<RegistrationApplicationResponse> listPendingRegistrations(
            int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<RegistrationApplicationResponse> list =
                identityManagementMapper.selectPendingRegistrationRows(offset, pageSize).stream()
                        .map(AdminConvert.INSTANCE::toRegistrationApplicationResponse)
                        .toList();
        long total = identityManagementMapper.countPendingRegistrations();
        return PageResult.of(list, total, page, pageSize);
    }

    @Transactional
    @Override
    public void reviewRegistration(Long operatorId, Long bindingId, String action) {
        Map<String, Object> binding = identityManagementMapper.selectIdentityBindingById(bindingId);
        if (binding == null || binding.isEmpty()) {
            throw new BusinessException(404, "registration application not found");
        }
        if (!RegistrationStatusEnum.PENDING
                .getCode()
                .equals(String.valueOf(binding.get("status")))) {
            throw new BusinessException(400, "registration application has already been reviewed");
        }

        String normalizedAction = normalizeAction(action);
        Long userId = Long.valueOf(String.valueOf(binding.get("userId")));
        Long memberId = Long.valueOf(String.valueOf(binding.get("memberId")));
        String identityType = String.valueOf(binding.get("identityType"));
        String targetStatus =
                "APPROVE".equals(normalizedAction)
                        ? AccountStatusEnum.ENABLED.getCode()
                        : RegistrationStatusEnum.REJECTED.getCode();

        identityManagementMapper.updateIdentityBindingStatus(bindingId, targetStatus, operatorId);
        identityManagementMapper.updateMemberAccountStatus(memberId, targetStatus, operatorId);
        updateProfileStatus(identityType, memberId, targetStatus, operatorId);

        if (AccountStatusEnum.ENABLED.getCode().equals(targetStatus)) {
            ensureRoleBinding(userId, identityType);
            if (identityManagementMapper.countIdentityBindingsByUserAndStatus(
                            userId, AccountStatusEnum.ENABLED.getCode())
                    == 1) {
                identityManagementMapper.updateUserActiveIdentity(
                        userId, memberId, identityType, operatorId);
            }
            // 审核通过时，从 profile 表回写显示名称
            syncDisplayName(userId, memberId, identityType, operatorId);
        }

        refreshRegisterStatus(userId, operatorId);
    }

    private void syncDisplayName(Long userId, Long memberId, String identityType, Long operatorId) {
        String displayName;
        if (MemberTypeEnum.MERCHANT.getCode().equals(identityType)) {
            displayName = identityManagementMapper.selectMerchantDisplayNameByMemberId(memberId);
        } else if (MemberTypeEnum.SUPPLIER.getCode().equals(identityType)) {
            displayName = identityManagementMapper.selectSupplierDisplayNameByMemberId(memberId);
        } else if (MemberTypeEnum.CUSTOMER.getCode().equals(identityType)) {
            displayName = identityManagementMapper.selectCustomerDisplayNameByMemberId(memberId);
        } else {
            displayName = null;
        }
        if (displayName != null && !displayName.isBlank()) {
            identityManagementMapper.updateMemberAccountName(
                    memberId, displayName.trim(), operatorId);
            identityManagementMapper.updateUserNickName(userId, displayName.trim(), operatorId);
        }
    }

    private void updateProfileStatus(
            String identityType, Long memberId, String status, Long operatorId) {
        if (MemberTypeEnum.CUSTOMER.getCode().equals(identityType)) {
            identityManagementMapper.updateCustomerInfoStatusByMemberId(
                    memberId, status, operatorId);
        } else if (MemberTypeEnum.MERCHANT.getCode().equals(identityType)) {
            identityManagementMapper.updateMerchantInfoStatusByMemberId(
                    memberId, status, operatorId);
        } else if (MemberTypeEnum.SUPPLIER.getCode().equals(identityType)) {
            identityManagementMapper.updateSupplierInfoStatusByMemberId(
                    memberId, status, operatorId);
        } else {
            throw new BusinessException(400, "unsupported identityType");
        }
    }

    private void ensureRoleBinding(Long userId, String identityType) {
        Long roleId = identityManagementMapper.selectRoleIdByCode(identityType);
        if (roleId == null) {
            throw new BusinessException(404, "role not found for identity: " + identityType);
        }
        if (identityManagementMapper.countUserRole(userId, roleId) == 0) {
            identityManagementMapper.insertUserRole(userId, roleId);
        }
    }

    private void refreshRegisterStatus(Long userId, Long operatorId) {
        String registerStatus;
        if (identityManagementMapper.countIdentityBindingsByUserAndStatus(
                        userId, RegistrationStatusEnum.PENDING.getCode())
                > 0) {
            registerStatus = RegistrationStatusEnum.PENDING.getCode();
        } else if (identityManagementMapper.countIdentityBindingsByUserAndStatus(
                        userId, AccountStatusEnum.ENABLED.getCode())
                > 0) {
            registerStatus = AccountStatusEnum.ENABLED.getCode();
        } else if (identityManagementMapper.countIdentityBindingsByUserAndStatus(
                        userId, RegistrationStatusEnum.REJECTED.getCode())
                > 0) {
            registerStatus = RegistrationStatusEnum.REJECTED.getCode();
        } else {
            registerStatus = AccountStatusEnum.DISABLED.getCode();
        }
        identityManagementMapper.updateUserRegisterStatus(userId, registerStatus, operatorId);
    }

    private String normalizeAction(String action) {
        if (action == null || action.isBlank()) {
            throw new BusinessException(400, "action is required");
        }
        String normalized = action.trim().toUpperCase();
        if (!List.of("APPROVE", "REJECT").contains(normalized)) {
            throw new BusinessException(400, "action must be APPROVE or REJECT");
        }
        return normalized;
    }
}
