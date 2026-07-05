package com.telecom.scm.member.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.dto.request.SaveSupplierAuthorizationRequest;
import com.telecom.scm.member.dto.request.SaveSupplierRelationRequest;
import com.telecom.scm.member.dto.response.OperationMessageResponse;
import com.telecom.scm.member.dto.response.SupplierOptionsResponse;
import com.telecom.scm.member.mapper.row.SupplierAuthorizationRow;
import com.telecom.scm.member.mapper.row.SupplierRelationRow;
import com.telecom.scm.member.service.SupplyCollaborationService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/member/supplier/cooperation")
public class SupplierCooperationController {

    private final SupplyCollaborationService supplyCollaborationService;
    private final PermissionGuard permissionGuard;

    public SupplierCooperationController(
            SupplyCollaborationService supplyCollaborationService,
            PermissionGuard permissionGuard) {
        this.supplyCollaborationService = supplyCollaborationService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/options")
    public ApiResponse<SupplierOptionsResponse> options(@CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "supplier:cooperation:view");
        return ApiResponse.success(supplyCollaborationService.supplierOptions(user.username()));
    }

    @GetMapping("/authorizations")
    public ApiResponse<PageResult<SupplierAuthorizationRow>> authorizations(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "supplier:cooperation:view");
        return ApiResponse.success(
                supplyCollaborationService.supplierAuthorizations(user.username(), page, pageSize));
    }

    @GetMapping("/relations")
    public ApiResponse<PageResult<SupplierRelationRow>> relations(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String status) {
        permissionGuard.require(user, "supplier:cooperation:view");
        return ApiResponse.success(
                supplyCollaborationService.supplierRelations(
                        user.username(), page, pageSize, status));
    }

    @PostMapping("/relations")
    @OperationAudit(module = "SupplierCooperation", businessType = "SAVE_SUPPLIER_RELATION")
    public ApiResponse<OperationMessageResponse> saveRelation(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveSupplierRelationRequest request) {
        permissionGuard.require(user, "supplier:cooperation:edit");
        return ApiResponse.success(
                supplyCollaborationService.saveSupplierRelation(user.username(), request));
    }

    @PostMapping("/authorizations")
    @OperationAudit(module = "SupplierCooperation", businessType = "SAVE_SUPPLIER_AUTHORIZATION")
    public ApiResponse<OperationMessageResponse> saveAuthorization(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveSupplierAuthorizationRequest request) {
        permissionGuard.require(user, "supplier:authorization:edit");
        return ApiResponse.success(
                supplyCollaborationService.saveSupplierAuthorization(user.username(), request));
    }

    @PutMapping("/relations/{relationId}")
    @OperationAudit(module = "SupplierCooperation", businessType = "HANDLE_SUPPLIER_RELATION")
    public ApiResponse<OperationMessageResponse> handleRelation(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long relationId,
            @Valid @RequestBody HandleSupplierRelationRequest request) {
        permissionGuard.require(user, "supplier:cooperation:edit");
        return ApiResponse.success(
                supplyCollaborationService.handleSupplierRelation(
                        user.username(), relationId, request.status(), request.remark()));
    }

    @PutMapping("/authorizations/{authorizationId}")
    @OperationAudit(module = "SupplierCooperation", businessType = "HANDLE_SUPPLIER_AUTHORIZATION")
    public ApiResponse<OperationMessageResponse> handleAuthorization(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long authorizationId,
            @Valid @RequestBody HandleSupplierAuthorizationRequest request) {
        permissionGuard.require(user, "supplier:cooperation:edit");
        return ApiResponse.success(
                supplyCollaborationService.handleSupplierAuthorization(
                        user.username(), authorizationId, request.status(), request.remark()));
    }
}
