package com.telecom.scm.security.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.common.enums.MemberTypeEnum;
import com.telecom.scm.common.enums.RegistrationStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.security.convert.SecurityConvert;
import com.telecom.scm.security.mapper.SecurityIdentityRow;
import com.telecom.scm.security.mapper.SecurityUserMapper;
import com.telecom.scm.security.mapper.SecurityUserRow;
import com.telecom.scm.security.model.AuthenticatedUser;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final SecurityUserMapper securityUserMapper;
    private final PasswordEncoder passwordEncoder;

    public UserAuthServiceImpl(
            SecurityUserMapper securityUserMapper, PasswordEncoder passwordEncoder) {
        this.securityUserMapper = securityUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<AuthenticatedUser> findByUsername(String username) {
        String preferredIdentity =
                securityUserMapper.selectLastActiveIdentityTypeByUsername(username);
        if (preferredIdentity != null && !preferredIdentity.isBlank()) {
            Optional<AuthenticatedUser> activeUser =
                    findByUsernameAndIdentity(username, preferredIdentity);
            if (activeUser.isPresent()) {
                return activeUser;
            }
        }

        return securityUserMapper.selectIdentityRowsByUsername(username).stream()
                .filter(
                        identity ->
                                AccountStatusEnum.ENABLED
                                        .getCode()
                                        .equalsIgnoreCase(identity.getStatus()))
                .map(identity -> findByUsernameAndIdentity(username, identity.getIdentityType()))
                .flatMap(Optional::stream)
                .findFirst();
    }

    @Override
    public Optional<AuthenticatedUser> findByUsernameAndIdentity(
            String username, String identityType) {
        if (identityType == null || identityType.isBlank()) {
            return Optional.empty();
        }
        String normalizedIdentity = identityType.trim().toUpperCase();
        SecurityUserRow row =
                MemberTypeEnum.ADMIN.getCode().equals(normalizedIdentity)
                        ? securityUserMapper.selectAdminByUsername(username)
                        : securityUserMapper.selectMemberIdentityByUsername(
                                username, normalizedIdentity);
        if (row == null) {
            return Optional.empty();
        }
        List<String> permissions =
                securityUserMapper.selectPermissionCodesByUsernameAndRole(
                        row.getUsername(), row.getRoleCode());
        if (permissions == null) {
            permissions = List.of();
        }
        return Optional.of(SecurityConvert.INSTANCE.toAuthenticatedUser(row, permissions));
    }

    @Override
    public AuthenticatedUser authenticate(String username, String rawPassword) {
        List<SecurityIdentityRow> identities =
                securityUserMapper.selectIdentityRowsByUsername(username);
        if (identities.isEmpty()) {
            throw new BusinessException(400, "username or password is invalid");
        }

        AuthenticatedUser user =
                findByUsername(username)
                        .orElseThrow(
                                () -> {
                                    boolean pendingOnly =
                                            identities.stream()
                                                    .anyMatch(
                                                            identity ->
                                                                    RegistrationStatusEnum.PENDING
                                                                            .getCode()
                                                                            .equalsIgnoreCase(
                                                                                    identity
                                                                                            .getStatus()));
                                    return new BusinessException(
                                            403,
                                            pendingOnly
                                                    ? "account is pending approval"
                                                    : "account has no enabled identity");
                                });

        if (!passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw new BusinessException(400, "username or password is invalid");
        }

        return user;
    }
}
