package com.telecom.scm.admin.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.telecom.scm.admin.dto.request.AssignRoleMenusRequest;
import com.telecom.scm.admin.dto.request.AssignUserMenusRequest;
import com.telecom.scm.admin.dto.request.StatusUpdateRequest;
import com.telecom.scm.admin.dto.response.ImportDataResponse;
import com.telecom.scm.admin.dto.response.ImportExportOverviewResponse;
import com.telecom.scm.admin.dto.response.MessageResponse;
import com.telecom.scm.admin.mapper.ImportExportLogRow;
import com.telecom.scm.admin.mapper.LoginLogRow;
import com.telecom.scm.admin.mapper.MenuRow;
import com.telecom.scm.admin.mapper.OperationLogRow;
import com.telecom.scm.admin.mapper.RoleRow;
import com.telecom.scm.admin.service.AdminGovernanceService;
import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
public class AdminGovernanceController {

    private final AdminGovernanceService adminGovernanceService;
    private final PermissionGuard permissionGuard;

    public AdminGovernanceController(
            AdminGovernanceService adminGovernanceService, PermissionGuard permissionGuard) {
        this.adminGovernanceService = adminGovernanceService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/api/admin/roles")
    public ApiResponse<PageResult<RoleRow>> roles(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        permissionGuard.require(user, "admin:role:view");
        return ApiResponse.success(adminGovernanceService.roles(page, pageSize));
    }

    @PutMapping("/api/admin/roles/{roleId}/status")
    @OperationAudit(module = "Admin", businessType = "UPDATE_ROLE_STATUS")
    public ApiResponse<MessageResponse> updateRoleStatus(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long roleId,
            @Valid @RequestBody StatusUpdateRequest request) {
        permissionGuard.require(user, "admin:role:assign");
        return ApiResponse.success(
                adminGovernanceService.updateRoleStatus(roleId, request.status()));
    }

    @GetMapping("/api/admin/menus")
    public ApiResponse<PageResult<MenuRow>> menus(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        permissionGuard.require(user, "admin:menu:view");
        return ApiResponse.success(adminGovernanceService.menus(page, pageSize));
    }

    @PutMapping("/api/admin/menus/{menuId}/status")
    @OperationAudit(module = "Admin", businessType = "UPDATE_MENU_STATUS")
    public ApiResponse<MessageResponse> updateMenuStatus(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long menuId,
            @Valid @RequestBody StatusUpdateRequest request) {
        permissionGuard.require(user, "admin:menu:assign");
        return ApiResponse.success(
                adminGovernanceService.updateMenuStatus(menuId, request.status()));
    }

    @GetMapping("/api/admin/roles/{roleId}/menus")
    public ApiResponse<List<String>> roleMenuIds(
            @CurrentUser AuthenticatedUser user, @PathVariable Long roleId) {
        permissionGuard.require(user, "admin:menu:view");
        return ApiResponse.success(adminGovernanceService.roleMenuIds(roleId));
    }

    @PutMapping("/api/admin/roles/{roleId}/menus")
    @OperationAudit(module = "Admin", businessType = "ASSIGN_ROLE_MENUS")
    public ApiResponse<List<String>> assignRoleMenus(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long roleId,
            @Valid @RequestBody AssignRoleMenusRequest request) {
        permissionGuard.require(user, "admin:menu:assign");
        return ApiResponse.success(
                adminGovernanceService.assignRoleMenus(roleId, request.menuIds()));
    }

    // 用户菜单权限管理
    @GetMapping("/api/admin/users/{userId}/menus")
    public ApiResponse<List<String>> userMenuIds(
            @CurrentUser AuthenticatedUser user, @PathVariable Long userId) {
        permissionGuard.require(user, "admin:user:view");
        return ApiResponse.success(adminGovernanceService.userMenuIds(userId));
    }

    @PutMapping("/api/admin/users/{userId}/menus")
    @OperationAudit(module = "Admin", businessType = "ASSIGN_USER_MENUS")
    public ApiResponse<List<String>> assignUserMenus(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long userId,
            @Valid @RequestBody AssignUserMenusRequest request) {
        permissionGuard.require(user, "admin:user:assign");
        return ApiResponse.success(
                adminGovernanceService.assignUserMenus(
                        userId, request.menuIds(), request.roleId()));
    }

    @GetMapping("/api/admin/login-logs")
    public ApiResponse<PageResult<LoginLogRow>> loginLogs(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        permissionGuard.require(user, "admin:login-log:view");
        return ApiResponse.success(adminGovernanceService.loginLogs(page, pageSize));
    }

    @GetMapping("/api/admin/operation-logs")
    public ApiResponse<PageResult<OperationLogRow>> operationLogs(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        permissionGuard.require(user, "admin:operation-log:view");
        return ApiResponse.success(adminGovernanceService.operationLogs(page, pageSize));
    }

    @GetMapping("/api/admin/import-export")
    public ApiResponse<ImportExportOverviewResponse> importExportOverview(
            @CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "admin:transfer:view");
        return ApiResponse.success(adminGovernanceService.importExportOverview());
    }

    @GetMapping("/api/admin/import-export/logs")
    public ApiResponse<PageResult<ImportExportLogRow>> importExportLogs(
            @CurrentUser AuthenticatedUser user,
            @RequestParam String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        permissionGuard.require(user, "admin:transfer:view");
        return ApiResponse.success(adminGovernanceService.importExportLogs(type, page, pageSize));
    }

    @PostMapping("/api/admin/import")
    @OperationAudit(module = "Admin", businessType = "IMPORT_DATA")
    public ApiResponse<ImportDataResponse> importData(
            @CurrentUser AuthenticatedUser user,
            @RequestParam String type,
            @RequestParam MultipartFile file) {
        permissionGuard.require(user, "admin:import:run");
        return ApiResponse.success(adminGovernanceService.importData(type, file));
    }

    @GetMapping("/api/admin/export")
    @OperationAudit(module = "Admin", businessType = "EXPORT_DATA")
    public ResponseEntity<byte[]> exportCsv(
            @CurrentUser AuthenticatedUser user,
            @RequestParam String type,
            @RequestParam(defaultValue = "false") boolean template) {
        permissionGuard.require(user, "admin:export:view");
        return adminGovernanceService.exportCsv(type, template);
    }
}
