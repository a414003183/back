package com.telecom.scm.security.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.common.enums.MemberTypeEnum;
import com.telecom.scm.common.enums.RegistrationStatusEnum;
import com.telecom.scm.common.enums.UserStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.security.convert.SecurityConvert;
import com.telecom.scm.security.dto.request.RegisterCustomerRequest;
import com.telecom.scm.security.dto.request.RegisterMerchantRequest;
import com.telecom.scm.security.dto.request.RegisterSupplierRequest;
import com.telecom.scm.security.dto.response.RegistrationSubmissionResponse;
import com.telecom.scm.security.dto.response.UserIdentityOption;
import com.telecom.scm.security.entity.CustomerInfoEntity;
import com.telecom.scm.security.entity.MemberAccountEntity;
import com.telecom.scm.security.entity.MerchantInfoEntity;
import com.telecom.scm.security.entity.SupplierInfoEntity;
import com.telecom.scm.security.entity.UserEntity;
import com.telecom.scm.security.entity.UserIdentityBindingEntity;
import com.telecom.scm.security.mapper.IdentityManagementMapper;
import com.telecom.scm.security.mapper.SecurityUserMapper;
import com.telecom.scm.security.model.AuthenticatedUser;

@Service
public class IdentitySessionServiceImpl implements IdentitySessionService {

    private static final Long SYSTEM_OPERATOR_ID = 1L;
    private static final AtomicInteger MEMBER_SEQUENCE = new AtomicInteger(5000);
    private static final DateTimeFormatter CODE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final IdentityManagementMapper identityManagementMapper;
    private final SecurityUserMapper securityUserMapper;
    private final UserAuthService userAuthService;
    private final PasswordEncoder passwordEncoder;

    public IdentitySessionServiceImpl(
            IdentityManagementMapper identityManagementMapper,
            SecurityUserMapper securityUserMapper,
            UserAuthService userAuthService,
            PasswordEncoder passwordEncoder) {
        this.identityManagementMapper = identityManagementMapper;
        this.securityUserMapper = securityUserMapper;
        this.userAuthService = userAuthService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserIdentityOption> listIdentities(AuthenticatedUser user) {
        return listIdentities(user.username(), user.identityType());
    }

    @Override
    public List<UserIdentityOption> listIdentities(String username, String activeIdentityType) {
        return securityUserMapper.selectIdentityRowsByUsername(username).stream()
                .map(
                        identity ->
                                SecurityConvert.INSTANCE.toUserIdentityOption(
                                        identity, activeIdentityType))
                .toList();
    }

    @Transactional
    @Override
    public AuthenticatedUser switchIdentity(AuthenticatedUser currentUser, String identityType) {
        String normalizedIdentity = normalizeIdentityType(identityType, true);
        // 校验当前用户是否拥有目标身份
        boolean hasTargetIdentity =
                listIdentities(currentUser).stream()
                        .anyMatch(
                                identity ->
                                        normalizedIdentity.equalsIgnoreCase(identity.identityType())
                                                && AccountStatusEnum.ENABLED
                                                        .getCode()
                                                        .equalsIgnoreCase(identity.status()));
        if (!hasTargetIdentity) {
            throw new BusinessException(
                    403, "identity is unavailable or not bound to current user");
        }
        AuthenticatedUser nextUser =
                userAuthService
                        .findByUsernameAndIdentity(currentUser.username(), normalizedIdentity)
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                403,
                                                "identity is unavailable or pending approval"));
        syncActiveIdentity(nextUser, currentUser.userId());
        return nextUser;
    }

    @Transactional
    @Override
    public void syncActiveIdentity(AuthenticatedUser user) {
        syncActiveIdentity(user, user.userId());
    }

    @Transactional
    @Override
    public RegistrationSubmissionResponse registerCustomer(RegisterCustomerRequest request) {
        registerMemberIdentity(
                MemberTypeEnum.CUSTOMER.getCode(),
                request.username(),
                request.password(),
                request.username(),
                request.contactPhone(),
                request.email(),
                true,
                (memberId, userId, autoApprove) -> {
                    String status =
                            autoApprove
                                    ? AccountStatusEnum.ENABLED.getCode()
                                    : RegistrationStatusEnum.PENDING.getCode();
                    String remark =
                            autoApprove
                                    ? "created by self registration"
                                    : "submitted for manual review";
                    CustomerInfoEntity param =
                            SecurityConvert.INSTANCE.toCustomerInfoEntity(
                                    memberId, status, remark, userId, request);
                    identityManagementMapper.insertCustomerInfo(param);
                });
        return RegistrationSubmissionResponse.of(AccountStatusEnum.ENABLED.getCode(), "注册成功");
    }

    @Transactional
    @Override
    public RegistrationSubmissionResponse registerMerchant(RegisterMerchantRequest request) {
        registerMemberIdentity(
                MemberTypeEnum.MERCHANT.getCode(),
                request.username(),
                request.password(),
                request.shopName(),
                request.contactPhone(),
                request.email(),
                false,
                (memberId, userId, autoApprove) -> {
                    String status =
                            autoApprove
                                    ? AccountStatusEnum.ENABLED.getCode()
                                    : RegistrationStatusEnum.PENDING.getCode();
                    String remark =
                            autoApprove
                                    ? "created by self registration"
                                    : "submitted for manual review";
                    MerchantInfoEntity param =
                            SecurityConvert.INSTANCE.toMerchantInfoEntity(
                                    memberId, status, remark, userId, request);
                    identityManagementMapper.insertMerchantInfo(param);
                });
        return RegistrationSubmissionResponse.of(
                RegistrationStatusEnum.PENDING.getCode(), "商家入驻申请已提交，请等待平台审核");
    }

    @Transactional
    @Override
    public RegistrationSubmissionResponse registerSupplier(RegisterSupplierRequest request) {
        registerMemberIdentity(
                MemberTypeEnum.SUPPLIER.getCode(),
                request.username(),
                request.password(),
                request.supplierName(),
                request.contactPhone(),
                request.email(),
                false,
                (memberId, userId, autoApprove) -> {
                    String status =
                            autoApprove
                                    ? AccountStatusEnum.ENABLED.getCode()
                                    : RegistrationStatusEnum.PENDING.getCode();
                    String remark =
                            autoApprove
                                    ? "created by self registration"
                                    : "submitted for manual review";
                    SupplierInfoEntity param =
                            SecurityConvert.INSTANCE.toSupplierInfoEntity(
                                    memberId, status, remark, userId, request);
                    identityManagementMapper.insertSupplierInfo(param);
                });
        return RegistrationSubmissionResponse.of(
                RegistrationStatusEnum.PENDING.getCode(), "供应商入驻申请已提交，请等待平台审核");
    }

    private void syncActiveIdentity(AuthenticatedUser user, Long updatedBy) {
        identityManagementMapper.updateUserActiveIdentity(
                user.userId(), user.memberId(), user.identityType(), updatedBy);
    }

    private void registerMemberIdentity(
            String identityType,
            String username,
            String rawPassword,
            String displayName,
            String phone,
            String email,
            boolean autoApprove,
            IdentityProfileWriter profileWriter) {
        String normalizedIdentity = normalizeIdentityType(identityType, false);
        String lockKey = normalizeUsername(username) + ":" + normalizedIdentity;
        synchronized (lockKey.intern()) {
            Map<String, Object> account =
                    identityManagementMapper.selectUserAccountByUsername(
                            normalizeUsername(username));
            Long userId;
            boolean hasEnabledIdentity;
            if (account == null || account.isEmpty()) {
                userId =
                        createUser(
                                normalizeUsername(username),
                                rawPassword,
                                displayName,
                                phone,
                                email,
                                autoApprove ? normalizedIdentity : null,
                                autoApprove);
                hasEnabledIdentity = false;
            } else {
                validateExistingAccount(account, rawPassword);
                userId = readLong(account.get("userId"));
                Map<String, Object> existingBinding =
                        identityManagementMapper.selectIdentityBindingByUserAndType(
                                userId, normalizedIdentity);
                if (existingBinding != null && !existingBinding.isEmpty()) {
                    throw new BusinessException(
                            400, "identity already exists or is pending review");
                }
                hasEnabledIdentity =
                        identityManagementMapper.countIdentityBindingsByUserAndStatus(
                                        userId, AccountStatusEnum.ENABLED.getCode())
                                > 0;
            }

            Long memberId =
                    createMemberProfile(
                            userId,
                            normalizedIdentity,
                            displayName,
                            phone,
                            email,
                            autoApprove,
                            profileWriter);
            insertIdentityBinding(
                    userId, normalizedIdentity, memberId, autoApprove, !hasEnabledIdentity);
            if (autoApprove) {
                ensureRoleBinding(userId, normalizedIdentity);
                if (!hasEnabledIdentity) {
                    identityManagementMapper.updateUserActiveIdentity(
                            userId, memberId, normalizedIdentity, SYSTEM_OPERATOR_ID);
                }
            }
            refreshRegisterStatus(userId);
        }
    }

    private Long createUser(
            String username,
            String rawPassword,
            String displayName,
            String phone,
            String email,
            String activeIdentityType,
            boolean enabled) {
        UserEntity param =
                SecurityConvert.INSTANCE.toUserEntity(
                        username,
                        normalizeText(displayName, username),
                        "MEMBER",
                        normalizeNullable(phone),
                        normalizeNullable(email),
                        UserStatusEnum.ENABLED,
                        "SELF_REGISTER",
                        enabled
                                ? AccountStatusEnum.ENABLED.getCode()
                                : RegistrationStatusEnum.PENDING.getCode(),
                        activeIdentityType,
                        SYSTEM_OPERATOR_ID,
                        SYSTEM_OPERATOR_ID);
        param.setPassword(passwordEncoder.encode(rawPassword.trim()));
        identityManagementMapper.insertUser(param);
        return param.getId();
    }

    private Long createMemberProfile(
            Long userId,
            String identityType,
            String displayName,
            String phone,
            String email,
            boolean autoApprove,
            IdentityProfileWriter profileWriter) {
        String status =
                autoApprove
                        ? AccountStatusEnum.ENABLED.getCode()
                        : RegistrationStatusEnum.PENDING.getCode();
        String remark =
                autoApprove ? "created by self registration" : "submitted for manual review";
        MemberAccountEntity param =
                SecurityConvert.INSTANCE.toMemberAccountEntity(
                        buildMemberCode(identityType),
                        identityType,
                        normalizeText(displayName, identityType + " USER"),
                        normalizeNullable(phone),
                        normalizeNullable(email),
                        status,
                        remark,
                        userId,
                        userId);
        identityManagementMapper.insertMemberAccount(param);
        Long memberId = param.getId();
        profileWriter.write(memberId, userId, autoApprove);
        return memberId;
    }

    private void insertIdentityBinding(
            Long userId,
            String identityType,
            Long memberId,
            boolean autoApprove,
            boolean defaultIdentity) {
        String status =
                autoApprove
                        ? AccountStatusEnum.ENABLED.getCode()
                        : RegistrationStatusEnum.PENDING.getCode();
        UserIdentityBindingEntity param =
                SecurityConvert.INSTANCE.toUserIdentityBindingEntity(
                        userId,
                        identityType,
                        memberId,
                        status,
                        defaultIdentity ? 1 : 0,
                        userId,
                        userId);
        identityManagementMapper.insertUserIdentityBinding(param);
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

    private void refreshRegisterStatus(Long userId) {
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
        identityManagementMapper.updateUserRegisterStatus(
                userId, registerStatus, SYSTEM_OPERATOR_ID);
    }

    private void validateExistingAccount(Map<String, Object> account, String rawPassword) {
        if (!AccountStatusEnum.ENABLED.getCode().equals(String.valueOf(account.get("status")))) {
            throw new BusinessException(403, "account is disabled");
        }
        if (MemberTypeEnum.ADMIN
                .getCode()
                .equalsIgnoreCase(String.valueOf(account.get("userType")))) {
            throw new BusinessException(403, "admin account cannot use front registration");
        }
        String passwordHash = String.valueOf(account.get("passwordHash"));
        if (!passwordEncoder.matches(rawPassword, passwordHash)) {
            throw new BusinessException(400, "username already exists with a different password");
        }
    }

    private String normalizeIdentityType(String identityType, boolean allowAdmin) {
        if (identityType == null || identityType.isBlank()) {
            throw new BusinessException(400, "identityType is required");
        }
        String normalized = identityType.trim().toUpperCase();
        List<String> allowed =
                allowAdmin
                        ? List.of(
                                MemberTypeEnum.ADMIN.getCode(),
                                MemberTypeEnum.CUSTOMER.getCode(),
                                MemberTypeEnum.MERCHANT.getCode(),
                                MemberTypeEnum.SUPPLIER.getCode())
                        : List.of(
                                MemberTypeEnum.CUSTOMER.getCode(),
                                MemberTypeEnum.MERCHANT.getCode(),
                                MemberTypeEnum.SUPPLIER.getCode());
        if (!allowed.contains(normalized)) {
            throw new BusinessException(400, "unsupported identityType");
        }
        return normalized;
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BusinessException(400, "username is required");
        }
        return username.trim();
    }

    private String normalizeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Long readLong(Object value) {
        return Long.valueOf(String.valueOf(value));
    }

    private String buildMemberCode(String identityType) {
        String prefix =
                switch (identityType) {
                    case "CUSTOMER" -> "CUS";
                    case "MERCHANT" -> "MER";
                    case "SUPPLIER" -> "SUP";
                    default -> "MEM";
                };
        return prefix
                + CODE_FORMATTER.format(LocalDateTime.now())
                + MEMBER_SEQUENCE.incrementAndGet();
    }

    private String buildInviteCode(String username) {
        String prefix =
                username == null || username.isBlank()
                        ? "USR"
                        : username.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (prefix.length() > 6) {
            prefix = prefix.substring(0, 6);
        }
        if (prefix.isBlank()) {
            prefix = "USR";
        }
        return prefix + MEMBER_SEQUENCE.incrementAndGet();
    }

    @FunctionalInterface
    private interface IdentityProfileWriter {
        void write(Long memberId, Long userId, boolean autoApprove);
    }
}
