package com.telecom.scm.admin.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.admin.dto.request.AssignAdminUserRoleRequest;
import com.telecom.scm.admin.dto.request.CreateAdminUserRequest;
import com.telecom.scm.admin.dto.request.ResetAdminUserPasswordRequest;
import com.telecom.scm.admin.dto.request.UpdateAdminUserStatusRequest;
import com.telecom.scm.admin.dto.response.AdminUserResponse;
import com.telecom.scm.admin.service.AdminUserService;
import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final PermissionGuard permissionGuard;

    public AdminUserController(AdminUserService adminUserService, PermissionGuard permissionGuard) {
        this.adminUserService = adminUserService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping
    public ApiResponse<PageResult<AdminUserResponse>> listUsers(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        permissionGuard.require(user, "admin:user:view");
        return ApiResponse.success(adminUserService.listUsers(page, pageSize));
    }

    @PostMapping
    @OperationAudit(module = "Admin", businessType = "CREATE_USER")
    public ApiResponse<AdminUserResponse> createUser(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody CreateAdminUserRequest request) {
        permissionGuard.require(user, "admin:user:view");
        return ApiResponse.success(adminUserService.createUser(user.userId(), request));
    }

    @PutMapping("/{userId}/role")
    @OperationAudit(module = "Admin", businessType = "ASSIGN_USER_ROLE")
    public ApiResponse<AdminUserResponse> assignRole(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long userId,
            @Valid @RequestBody AssignAdminUserRoleRequest request) {
        permissionGuard.require(user, "admin:role:assign");
        return ApiResponse.success(adminUserService.assignRole(user.userId(), userId, request));
    }

    @PutMapping("/{userId}/status")
    @OperationAudit(module = "Admin", businessType = "UPDATE_USER_STATUS")
    public ApiResponse<AdminUserResponse> updateStatus(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateAdminUserStatusRequest request) {
        permissionGuard.require(user, "admin:user:view");
        return ApiResponse.success(adminUserService.updateStatus(user.userId(), userId, request));
    }

    @PutMapping("/{userId}/password")
    @OperationAudit(module = "Admin", businessType = "RESET_USER_PASSWORD")
    public ApiResponse<Void> resetPassword(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long userId,
            @Valid @RequestBody ResetAdminUserPasswordRequest request) {
        permissionGuard.require(user, "admin:user:view");
        adminUserService.resetPassword(user.userId(), userId, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{userId}")
    @OperationAudit(module = "Admin", businessType = "DELETE_USER")
    public ApiResponse<Void> deleteUser(
            @CurrentUser AuthenticatedUser user, @PathVariable Long userId) {
        permissionGuard.require(user, "admin:user:view");
        adminUserService.deleteUser(user.userId(), userId);
        return ApiResponse.success();
    }
}
