package com.telecom.scm.member.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.dto.request.ApplyMerchantAuthorizationRequest;
import com.telecom.scm.member.dto.request.ApplyMerchantRelationRequest;
import com.telecom.scm.member.dto.request.ImportMerchantSupplyRequest;
import com.telecom.scm.member.dto.response.OperationMessageResponse;
import com.telecom.scm.member.mapper.row.MerchantGoodsRow;
import com.telecom.scm.member.mapper.row.MerchantSupplyCatalogRow;
import com.telecom.scm.member.mapper.row.MerchantSupplyRelationRow;
import com.telecom.scm.member.service.SupplyCollaborationService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/member/merchant/supply")
public class MerchantSupplyController {

    private final SupplyCollaborationService supplyCollaborationService;
    private final PermissionGuard permissionGuard;

    public MerchantSupplyController(
            SupplyCollaborationService supplyCollaborationService,
            PermissionGuard permissionGuard) {
        this.supplyCollaborationService = supplyCollaborationService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping
    public ApiResponse<PageResult<MerchantSupplyRelationRow>> relations(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "merchant:supply:view");
        return ApiResponse.success(
                supplyCollaborationService.merchantSupplyRelations(
                        user.username(), page, pageSize));
    }

    @GetMapping("/catalog")
    public ApiResponse<PageResult<MerchantSupplyCatalogRow>> catalog(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "merchant:supply:view");
        return ApiResponse.success(
                supplyCollaborationService.merchantSupplyCatalog(user.username(), page, pageSize));
    }

    @PostMapping("/import")
    @OperationAudit(module = "MerchantSupply", businessType = "IMPORT_SUPPLIER_SKU")
    public ApiResponse<MerchantGoodsRow> importSupplierSku(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody ImportMerchantSupplyRequest request) {
        permissionGuard.require(user, "merchant:supply:import");
        return ApiResponse.success(
                supplyCollaborationService.importMerchantSupply(user.username(), request));
    }

    @PostMapping("/relations")
    @OperationAudit(module = "MerchantSupply", businessType = "APPLY_MERCHANT_RELATION")
    public ApiResponse<OperationMessageResponse> applyRelation(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody ApplyMerchantRelationRequest request) {
        permissionGuard.require(user, "merchant:supply:view");
        return ApiResponse.success(
                supplyCollaborationService.applyMerchantRelation(user.username(), request));
    }

    @PostMapping("/authorizations")
    @OperationAudit(module = "MerchantSupply", businessType = "APPLY_MERCHANT_AUTHORIZATION")
    public ApiResponse<OperationMessageResponse> applyAuthorization(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody ApplyMerchantAuthorizationRequest request) {
        permissionGuard.require(user, "merchant:supply:view");
        return ApiResponse.success(
                supplyCollaborationService.applyMerchantAuthorization(user.username(), request));
    }
}
