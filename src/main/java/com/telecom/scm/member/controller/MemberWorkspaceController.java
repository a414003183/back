package com.telecom.scm.member.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.dto.request.SaveCustomerProfileRequest;
import com.telecom.scm.member.dto.request.SaveMerchantProfileRequest;
import com.telecom.scm.member.dto.request.SaveSupplierProfileRequest;
import com.telecom.scm.member.dto.response.MerchantReportResponse;
import com.telecom.scm.member.mapper.row.CustomerProfileRow;
import com.telecom.scm.member.mapper.row.MerchantProfileRow;
import com.telecom.scm.member.mapper.row.MerchantShipmentRow;
import com.telecom.scm.member.mapper.row.SupplierProfileRow;
import com.telecom.scm.member.mapper.row.SupplierRelationRow;
import com.telecom.scm.member.mapper.row.SupplierStockRow;
import com.telecom.scm.member.service.MemberWorkspaceService;
import com.telecom.scm.member.service.SupplyCollaborationService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
public class MemberWorkspaceController {

    private final MemberWorkspaceService memberWorkspaceService;
    private final SupplyCollaborationService supplyCollaborationService;
    private final PermissionGuard permissionGuard;

    public MemberWorkspaceController(
            MemberWorkspaceService memberWorkspaceService,
            SupplyCollaborationService supplyCollaborationService,
            PermissionGuard permissionGuard) {
        this.memberWorkspaceService = memberWorkspaceService;
        this.supplyCollaborationService = supplyCollaborationService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/api/member/customer/profile")
    public ApiResponse<CustomerProfileRow> customerProfile(@CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "customer:profile:view");
        return ApiResponse.success(memberWorkspaceService.customerProfile(user.username()));
    }

    @PostMapping("/api/member/customer/profile")
    @OperationAudit(module = "CustomerProfile", businessType = "SAVE_CUSTOMER_PROFILE")
    public ApiResponse<CustomerProfileRow> saveCustomerProfile(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveCustomerProfileRequest request) {
        permissionGuard.require(user, "customer:profile:view");
        return ApiResponse.success(
                memberWorkspaceService.saveCustomerProfile(user.username(), request));
    }

    @GetMapping("/api/member/merchant/profile")
    public ApiResponse<MerchantProfileRow> merchantProfile(@CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "merchant:profile:view");
        return ApiResponse.success(memberWorkspaceService.merchantProfile(user.username()));
    }

    @PostMapping("/api/member/merchant/profile")
    @OperationAudit(module = "MerchantProfile", businessType = "SAVE_MERCHANT_PROFILE")
    public ApiResponse<MerchantProfileRow> saveMerchantProfile(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveMerchantProfileRequest request) {
        permissionGuard.require(user, "merchant:profile:view");
        return ApiResponse.success(
                memberWorkspaceService.saveMerchantProfile(user.username(), request));
    }

    @GetMapping("/api/member/merchant/shipping")
    public ApiResponse<PageResult<MerchantShipmentRow>> merchantShipping(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "merchant:order:ship");
        return ApiResponse.success(
                memberWorkspaceService.merchantShipments(user.username(), page, pageSize));
    }

    @GetMapping("/api/member/merchant/reports")
    public ApiResponse<MerchantReportResponse> merchantReports(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        permissionGuard.require(user, "merchant:report:view");
        return ApiResponse.success(
                memberWorkspaceService.merchantReports(
                        user.username(),
                        user.hasPermission("merchant:profit:view"),
                        startDate,
                        endDate));
    }

    @GetMapping("/api/member/merchant/reports/export")
    @OperationAudit(module = "MerchantReport", businessType = "EXPORT_MERCHANT_REPORT")
    public ResponseEntity<byte[]> exportMerchantReports(
            @CurrentUser AuthenticatedUser user,
            @RequestParam String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        permissionGuard.require(user, "merchant:report:export");
        return memberWorkspaceService.exportMerchantReport(
                user.username(),
                user.hasPermission("merchant:profit:view"),
                type,
                startDate,
                endDate);
    }

    @GetMapping("/api/member/supplier/profile")
    public ApiResponse<SupplierProfileRow> supplierProfile(@CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "supplier:profile:view");
        return ApiResponse.success(memberWorkspaceService.supplierProfile(user.username()));
    }

    @PostMapping("/api/member/supplier/profile")
    @OperationAudit(module = "SupplierProfile", businessType = "SAVE_SUPPLIER_PROFILE")
    public ApiResponse<SupplierProfileRow> saveSupplierProfile(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveSupplierProfileRequest request) {
        permissionGuard.require(user, "supplier:profile:view");
        return ApiResponse.success(
                memberWorkspaceService.saveSupplierProfile(user.username(), request));
    }

    @GetMapping("/api/member/supplier/stock")
    public ApiResponse<PageResult<SupplierStockRow>> supplierStock(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "supplier:stock:view");
        return ApiResponse.success(
                memberWorkspaceService.supplierStocks(user.username(), page, pageSize));
    }

    @GetMapping("/api/member/supplier/cooperation")
    public ApiResponse<PageResult<SupplierRelationRow>> supplierCooperation(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String status) {
        permissionGuard.require(user, "supplier:cooperation:view");
        return ApiResponse.success(
                supplyCollaborationService.supplierRelations(
                        user.username(), page, pageSize, status));
    }
}
