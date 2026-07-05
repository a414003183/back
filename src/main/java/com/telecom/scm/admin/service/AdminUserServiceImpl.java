package com.telecom.scm.admin.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.admin.convert.AdminConvert;
import com.telecom.scm.admin.dto.request.AssignAdminUserRoleRequest;
import com.telecom.scm.admin.dto.request.CreateAdminUserRequest;
import com.telecom.scm.admin.dto.request.ResetAdminUserPasswordRequest;
import com.telecom.scm.admin.dto.request.UpdateAdminUserStatusRequest;
import com.telecom.scm.admin.dto.response.AdminUserResponse;
import com.telecom.scm.admin.mapper.AdminUserMapper;
import com.telecom.scm.admin.mapper.AdminUserRow;
import com.telecom.scm.admin.mapper.RoleContextRow;
import com.telecom.scm.admin.mapper.UserContextRow;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.common.enums.MemberTypeEnum;
import com.telecom.scm.common.enums.UserStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.security.entity.CustomerInfoEntity;
import com.telecom.scm.security.entity.MemberAccountEntity;
import com.telecom.scm.security.entity.MerchantInfoEntity;
import com.telecom.scm.security.entity.SupplierInfoEntity;
import com.telecom.scm.security.entity.UserEntity;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final AtomicInteger MEMBER_SEQUENCE = new AtomicInteger(1000);
    private static final DateTimeFormatter CODE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(AdminUserMapper adminUserMapper, PasswordEncoder passwordEncoder) {
        this.adminUserMapper = adminUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResult<AdminUserResponse> listUsers(int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<AdminUserResponse> list =
                adminUserMapper.selectAdminUsers(offset, pageSize).stream()
                        .map(AdminConvert.INSTANCE::toAdminUserResponse)
                        .toList();
        long total = adminUserMapper.countAdminUsers();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserResponse createUser(Long operatorId, CreateAdminUserRequest request) {
        String username = request.username().trim();
        if (adminUserMapper.countUserByUsername(username) > 0) {
            throw new BusinessException(400, "username already exists");
        }

        String roleCode = normalizeRoleCode(request.roleCode());
        RoleContextRow role = requireRoleByCode(roleCode);
        String displayName = normalizeText(request.displayName(), username);
        UserStatusEnum status =
                request.status() != null ? request.status() : UserStatusEnum.ENABLED;
        String phone = normalizeNullable(request.phone());
        String email = normalizeNullable(request.email());
        Long memberId =
                MemberTypeEnum.ADMIN.getCode().equals(roleCode)
                        ? null
                        : createMemberProfile(
                                operatorId, roleCode, displayName, phone, email, username);

        UserEntity payload = new UserEntity();
        payload.setUsername(username);
        payload.setPassword(passwordEncoder.encode(request.password().trim()));
        payload.setNickName(displayName);
        payload.setUserType(
                MemberTypeEnum.ADMIN.getCode().equals(roleCode)
                        ? MemberTypeEnum.ADMIN.getCode()
                        : "MEMBER");
        payload.setMemberId(memberId);
        payload.setPhone(phone);
        payload.setEmail(email);
        payload.setStatus(status);
        payload.setCreatedBy(operatorId);
        payload.setUpdatedBy(operatorId);
        adminUserMapper.insertUser(payload);
        Long userId = payload.getId();
        adminUserMapper.insertUserRole(userId, role.getId());
        return requireUserResponse(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserResponse assignRole(
            Long operatorId, Long userId, AssignAdminUserRoleRequest request) {
        UserContextRow context = requireUserContext(userId);
        RoleContextRow role = requireRoleById(request.roleId());
        String roleCode = role.getRoleCode();
        String displayName =
                normalizeText(valueOrNull(context.getDisplayName()), context.getUsername());
        String phone = valueOrNull(context.getPhone());
        String email = valueOrNull(context.getEmail());
        Long memberId = context.getMemberId();

        if (!MemberTypeEnum.ADMIN.getCode().equals(roleCode)) {
            if (memberId == null) {
                memberId =
                        createMemberProfile(
                                operatorId,
                                roleCode,
                                displayName,
                                phone,
                                email,
                                context.getUsername());
            } else {
                updateMemberProfile(
                        operatorId,
                        memberId,
                        roleCode,
                        displayName,
                        phone,
                        email,
                        context.getUsername());
            }
        }

        UserEntity payload = new UserEntity();
        payload.setId(userId);
        payload.setMemberId(memberId);
        payload.setUserType(
                MemberTypeEnum.ADMIN.getCode().equals(roleCode)
                        ? MemberTypeEnum.ADMIN.getCode()
                        : "MEMBER");
        payload.setNickName(displayName);
        payload.setPhone(phone);
        payload.setEmail(email);
        payload.setUpdatedBy(operatorId);
        adminUserMapper.updateUserRoleProfile(payload);
        adminUserMapper.deleteUserRoles(userId);
        adminUserMapper.insertUserRole(userId, request.roleId());
        return requireUserResponse(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserResponse updateStatus(
            Long operatorId, Long userId, UpdateAdminUserStatusRequest request) {
        requireUserContext(userId);
        adminUserMapper.updateUserStatus(userId, request.status().getCode(), operatorId);
        return requireUserResponse(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long operatorId, Long userId, ResetAdminUserPasswordRequest request) {
        requireUserContext(userId);
        adminUserMapper.updateUserPassword(
                userId, passwordEncoder.encode(request.password().trim()), operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long operatorId, Long userId) {
        requireUserContext(userId);
        adminUserMapper.deleteUser(userId, operatorId);
    }

    private RoleContextRow requireRoleById(Long roleId) {
        RoleContextRow role = adminUserMapper.selectRoleById(roleId);
        if (role == null) {
            throw new BusinessException(404, "role not found");
        }
        if (!AccountStatusEnum.ENABLED.getCode().equals(role.getStatus())) {
            throw new BusinessException(400, "role is disabled");
        }
        return role;
    }

    private RoleContextRow requireRoleByCode(String roleCode) {
        RoleContextRow role = adminUserMapper.selectRoleByCode(roleCode);
        if (role == null) {
            throw new BusinessException(404, "role not found");
        }
        if (!AccountStatusEnum.ENABLED.getCode().equals(role.getStatus())) {
            throw new BusinessException(400, "role is disabled");
        }
        return role;
    }

    private UserContextRow requireUserContext(Long userId) {
        UserContextRow context = adminUserMapper.selectUserContextById(userId);
        if (context == null) {
            throw new BusinessException(404, "user not found");
        }
        return context;
    }

    private AdminUserResponse requireUserResponse(Long userId) {
        AdminUserRow row = adminUserMapper.selectAdminUserById(userId);
        if (row == null) {
            throw new BusinessException(404, "user not found");
        }
        return AdminConvert.INSTANCE.toAdminUserResponse(row);
    }

    private Long createMemberProfile(
            Long operatorId,
            String roleCode,
            String displayName,
            String phone,
            String email,
            String username) {
        MemberAccountEntity member = new MemberAccountEntity();
        member.setMemberCode(buildMemberCode(roleCode));
        member.setMemberType(roleCode);
        member.setMemberName(displayName);
        member.setPhone(phone);
        member.setEmail(email);
        member.setRemark("created by admin user management");
        member.setCreatedBy(operatorId);
        member.setUpdatedBy(operatorId);
        adminUserMapper.insertMemberAccount(member);
        Long memberId = member.getId();
        updateMemberProfile(operatorId, memberId, roleCode, displayName, phone, email, username);
        return memberId;
    }

    private void updateMemberProfile(
            Long operatorId,
            Long memberId,
            String roleCode,
            String displayName,
            String phone,
            String email,
            String username) {
        MemberAccountEntity member = new MemberAccountEntity();
        member.setId(memberId);
        member.setMemberType(roleCode);
        member.setMemberName(displayName);
        member.setPhone(phone);
        member.setEmail(email);
        member.setUpdatedBy(operatorId);
        adminUserMapper.updateMemberAccount(member);

        String contactPhone = phone == null ? "13800000000" : phone;
        String inviteCode = buildInviteCode(username);
        String remark = "generated by admin role assignment";

        switch (roleCode) {
            case "CUSTOMER" -> {
                if (adminUserMapper.selectCustomerInfoIdByMemberId(memberId) == null) {
                    CustomerInfoEntity profile = new CustomerInfoEntity();
                    profile.setMemberId(memberId);
                    profile.setCompanyName(displayName);
                    profile.setContactName(displayName);
                    profile.setContactPhone(contactPhone);
                    profile.setInviteCode(inviteCode);
                    profile.setRemark(remark);
                    profile.setCreatedBy(operatorId);
                    profile.setUpdatedBy(operatorId);
                    adminUserMapper.insertCustomerInfo(profile);
                }
            }
            case "MERCHANT" -> {
                if (adminUserMapper.selectMerchantInfoIdByMemberId(memberId) == null) {
                    MerchantInfoEntity profile = new MerchantInfoEntity();
                    profile.setMemberId(memberId);
                    profile.setShopName(displayName);
                    profile.setContactName(displayName);
                    profile.setContactPhone(contactPhone);
                    profile.setRemark(remark);
                    profile.setCreatedBy(operatorId);
                    profile.setUpdatedBy(operatorId);
                    adminUserMapper.insertMerchantInfo(profile);
                }
            }
            case "SUPPLIER" -> {
                if (adminUserMapper.selectSupplierInfoIdByMemberId(memberId) == null) {
                    SupplierInfoEntity profile = new SupplierInfoEntity();
                    profile.setMemberId(memberId);
                    profile.setSupplierName(displayName);
                    profile.setContactName(displayName);
                    profile.setContactPhone(contactPhone);
                    profile.setRemark(remark);
                    profile.setCreatedBy(operatorId);
                    profile.setUpdatedBy(operatorId);
                    adminUserMapper.insertSupplierInfo(profile);
                }
            }
            default -> throw new BusinessException(400, "unsupported role type");
        }
    }

    private String buildMemberCode(String roleCode) {
        return roleCode.substring(0, Math.min(3, roleCode.length()))
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

    private String normalizeRoleCode(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            throw new BusinessException(400, "roleCode is required");
        }
        String normalized = roleCode.trim().toUpperCase();
        if (!List.of(
                        MemberTypeEnum.ADMIN.getCode(),
                        MemberTypeEnum.CUSTOMER.getCode(),
                        MemberTypeEnum.MERCHANT.getCode(),
                        MemberTypeEnum.SUPPLIER.getCode())
                .contains(normalized)) {
            throw new BusinessException(400, "unsupported roleCode");
        }
        return normalized;
    }

    private String normalizeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String valueOrNull(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() || "null".equalsIgnoreCase(text) ? null : text;
    }
}
