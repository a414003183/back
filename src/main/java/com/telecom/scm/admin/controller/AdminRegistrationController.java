package com.telecom.scm.admin.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.admin.dto.request.RegistrationReviewRequest;
import com.telecom.scm.admin.dto.response.RegistrationApplicationResponse;
import com.telecom.scm.admin.service.AdminRegistrationService;
import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/admin/registrations")
public class AdminRegistrationController {

    private final AdminRegistrationService adminRegistrationService;
    private final PermissionGuard permissionGuard;

    public AdminRegistrationController(
            AdminRegistrationService adminRegistrationService, PermissionGuard permissionGuard) {
        this.adminRegistrationService = adminRegistrationService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping
    public ApiResponse<PageResult<RegistrationApplicationResponse>> listPendingRegistrations(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        permissionGuard.require(user, "admin:registration:view");
        return ApiResponse.success(
                adminRegistrationService.listPendingRegistrations(page, pageSize));
    }

    @PostMapping("/{bindingId}/review")
    @OperationAudit(module = "Admin", businessType = "REVIEW_REGISTRATION")
    public ApiResponse<Void> reviewRegistration(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long bindingId,
            @Valid @RequestBody RegistrationReviewRequest request) {
        permissionGuard.require(user, "admin:registration:review");
        adminRegistrationService.reviewRegistration(user.userId(), bindingId, request.action());
        return ApiResponse.success();
    }
}
