package com.telecom.scm.security.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.audit.service.AuditLogService;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.enums.MemberTypeEnum;
import com.telecom.scm.member.mapper.MemberWorkspaceMapper;
import com.telecom.scm.member.mapper.row.CustomerContextRow;
import com.telecom.scm.security.dto.request.LoginRequest;
import com.telecom.scm.security.dto.request.RegisterCustomerRequest;
import com.telecom.scm.security.dto.request.RegisterMerchantRequest;
import com.telecom.scm.security.dto.request.RegisterSupplierRequest;
import com.telecom.scm.security.dto.request.SwitchIdentityRequest;
import com.telecom.scm.security.dto.response.CurrentUserMenuItem;
import com.telecom.scm.security.dto.response.CurrentUserResponse;
import com.telecom.scm.security.dto.response.LoginResponse;
import com.telecom.scm.security.dto.response.RegistrationSubmissionResponse;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.CurrentUserMenuService;
import com.telecom.scm.security.service.IdentitySessionService;
import com.telecom.scm.security.service.TokenService;
import com.telecom.scm.security.service.UserAuthService;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UserAuthService userAuthService;
    private final TokenService tokenService;
    private final AuditLogService auditLogService;
    private final CurrentUserMenuService currentUserMenuService;
    private final IdentitySessionService identitySessionService;
    private final MemberWorkspaceMapper memberWorkspaceMapper;

    public AuthController(
            UserAuthService userAuthService,
            TokenService tokenService,
            AuditLogService auditLogService,
            CurrentUserMenuService currentUserMenuService,
            IdentitySessionService identitySessionService,
            MemberWorkspaceMapper memberWorkspaceMapper) {
        this.userAuthService = userAuthService;
        this.tokenService = tokenService;
        this.auditLogService = auditLogService;
        this.currentUserMenuService = currentUserMenuService;
        this.identitySessionService = identitySessionService;
        this.memberWorkspaceMapper = memberWorkspaceMapper;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        try {
            LOGGER.info("Login attempt for username: {}", request.username());
            AuthenticatedUser user =
                    userAuthService.authenticate(request.username(), request.password());
            identitySessionService.syncActiveIdentity(user);
            auditLogService.recordLogin(
                    user.userId(),
                    user.username(),
                    httpServletRequest.getRemoteAddr(),
                    "SUCCESS",
                    "login success");
            String token = tokenService.generateToken(user);
            LOGGER.info(
                    "Login successful for username: {}, role: {}", user.username(), user.role());
            LOGGER.info("Calling toLoginResponse...");
            LoginResponse response = toLoginResponse(user, token);
            LOGGER.info("toLoginResponse returned {} menus", response.menus().size());
            return ApiResponse.success(response);
        } catch (RuntimeException exception) {
            auditLogService.recordLogin(
                    null,
                    request.username(),
                    httpServletRequest.getRemoteAddr(),
                    "FAIL",
                    exception.getMessage());
            throw exception;
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(@CurrentUser AuthenticatedUser user) {
        return ApiResponse.success(toCurrentUserResponse(user));
    }

    @PostMapping("/switch-identity")
    @OperationAudit(module = "Auth", businessType = "SWITCH_IDENTITY")
    public ApiResponse<LoginResponse> switchIdentity(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SwitchIdentityRequest request) {
        AuthenticatedUser nextUser =
                identitySessionService.switchIdentity(user, request.identityType());
        String token = tokenService.generateToken(nextUser);
        return ApiResponse.success(toLoginResponse(nextUser, token));
    }

    @PostMapping("/register/customer")
    @OperationAudit(module = "Auth", businessType = "REGISTER_CUSTOMER")
    public ApiResponse<RegistrationSubmissionResponse> registerCustomer(
            @Valid @RequestBody RegisterCustomerRequest request) {
        return ApiResponse.success(identitySessionService.registerCustomer(request));
    }

    @PostMapping("/register/merchant")
    @OperationAudit(module = "Auth", businessType = "REGISTER_MERCHANT")
    public ApiResponse<RegistrationSubmissionResponse> registerMerchant(
            @Valid @RequestBody RegisterMerchantRequest request) {
        return ApiResponse.success(identitySessionService.registerMerchant(request));
    }

    @PostMapping("/register/supplier")
    @OperationAudit(module = "Auth", businessType = "REGISTER_SUPPLIER")
    public ApiResponse<RegistrationSubmissionResponse> registerSupplier(
            @Valid @RequestBody RegisterSupplierRequest request) {
        return ApiResponse.success(identitySessionService.registerSupplier(request));
    }

    private LoginResponse toLoginResponse(@CurrentUser AuthenticatedUser user, String token) {
        List<CurrentUserMenuItem> menus = currentUserMenuService.listMenus(user);
        String route = resolveRoute(user, menus);
        return new LoginResponse(
                token,
                "Bearer",
                user.username(),
                user.role(),
                user.identityType(),
                user.displayName(),
                route,
                user.permissions(),
                menus,
                identitySessionService.listIdentities(user));
    }

    private CurrentUserResponse toCurrentUserResponse(@CurrentUser AuthenticatedUser user) {
        List<CurrentUserMenuItem> menus = currentUserMenuService.listMenus(user);
        String route = resolveRoute(user, menus);
        String memberLevel = null;
        // 如果是客户身份，获取客户等级
        if (MemberTypeEnum.CUSTOMER.getCode().equals(user.identityType())) {
            try {
                CustomerContextRow context =
                        memberWorkspaceMapper.selectCustomerContextByUsername(user.username());
                if (context != null && context.getCustomerId() != null) {
                    memberLevel = context.getMemberLevel();
                }
            } catch (Exception e) {
                LOGGER.warn("获取客户等级失败: {}", e.getMessage());
            }
        }
        return new CurrentUserResponse(
                user.username(),
                user.role(),
                user.identityType(),
                user.displayName(),
                route,
                user.permissions(),
                menus,
                identitySessionService.listIdentities(user),
                memberLevel);
    }

    private String resolveRoute(
            @CurrentUser AuthenticatedUser user, List<CurrentUserMenuItem> menus) {
        if (menus != null) {
            for (CurrentUserMenuItem menu : menus) {
                if (menu.visible() && menu.path() != null && !menu.path().isBlank()) {
                    return menu.path();
                }
            }
            for (CurrentUserMenuItem menu : menus) {
                if (menu.path() != null && !menu.path().isBlank()) {
                    return menu.path();
                }
            }
        }
        return user.route();
    }
}
