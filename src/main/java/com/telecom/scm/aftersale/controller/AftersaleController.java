package com.telecom.scm.aftersale.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.aftersale.dto.request.ApproveAftersaleRequest;
import com.telecom.scm.aftersale.dto.request.CreateAftersaleRequest;
import com.telecom.scm.aftersale.dto.request.RefundAftersaleRequest;
import com.telecom.scm.aftersale.dto.request.RegisterReturnShipmentRequest;
import com.telecom.scm.aftersale.dto.request.RejectAftersaleRequest;
import com.telecom.scm.aftersale.dto.response.AftersaleActionResponse;
import com.telecom.scm.aftersale.dto.response.AftersaleSummaryResponse;
import com.telecom.scm.aftersale.service.AftersaleCommandService;
import com.telecom.scm.aftersale.service.AftersaleQueryService;
import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
public class AftersaleController {

    private final AftersaleQueryService aftersaleQueryService;
    private final AftersaleCommandService aftersaleCommandService;
    private final PermissionGuard permissionGuard;

    public AftersaleController(
            AftersaleQueryService aftersaleQueryService,
            AftersaleCommandService aftersaleCommandService,
            PermissionGuard permissionGuard) {
        this.aftersaleQueryService = aftersaleQueryService;
        this.aftersaleCommandService = aftersaleCommandService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/api/member/customer/aftersales")
    public ApiResponse<PageResult<AftersaleSummaryResponse>> customerAftersales(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "customer:aftersale:view");
        return ApiResponse.success(
                aftersaleQueryService.currentCustomerAftersales(user.username(), page, pageSize));
    }

    @PostMapping("/api/member/customer/aftersales")
    @OperationAudit(module = "Aftersale", businessType = "CREATE_AFTERSALE")
    public ApiResponse<AftersaleActionResponse> createCustomerAftersale(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody CreateAftersaleRequest request) {
        permissionGuard.require(user, "customer:aftersale:view");
        return ApiResponse.success(
                aftersaleCommandService.createCustomerAftersale(user.username(), request));
    }

    @PostMapping("/api/member/customer/aftersales/{aftersaleId}/return-shipment")
    @OperationAudit(module = "Aftersale", businessType = "REGISTER_RETURN_SHIPMENT")
    public ApiResponse<AftersaleActionResponse> registerCustomerReturnShipment(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long aftersaleId,
            @Valid @RequestBody RegisterReturnShipmentRequest request) {
        permissionGuard.require(user, "customer:aftersale:view");
        return ApiResponse.success(
                aftersaleCommandService.registerCustomerReturnShipment(
                        user.username(), aftersaleId, request));
    }

    @GetMapping("/api/member/merchant/aftersales")
    public ApiResponse<PageResult<AftersaleSummaryResponse>> merchantAftersales(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "merchant:aftersale:view");
        return ApiResponse.success(
                aftersaleQueryService.currentMerchantAftersales(user.username(), page, pageSize));
    }

    @PostMapping("/api/member/merchant/aftersales/{aftersaleId}/approve")
    @OperationAudit(module = "Aftersale", businessType = "APPROVE_AFTERSALE")
    public ApiResponse<AftersaleActionResponse> approveMerchantAftersale(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long aftersaleId,
            @Valid @RequestBody ApproveAftersaleRequest request) {
        permissionGuard.require(user, "merchant:aftersale:approve");
        return ApiResponse.success(
                aftersaleCommandService.approveMerchantAftersale(
                        user.username(), aftersaleId, request));
    }

    @PostMapping("/api/member/merchant/aftersales/{aftersaleId}/reject")
    @OperationAudit(module = "Aftersale", businessType = "REJECT_AFTERSALE")
    public ApiResponse<AftersaleActionResponse> rejectMerchantAftersale(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long aftersaleId,
            @RequestBody(required = false) RejectAftersaleRequest request) {
        permissionGuard.require(user, "merchant:aftersale:reject");
        RejectAftersaleRequest actualRequest =
                request == null ? new RejectAftersaleRequest(null) : request;
        return ApiResponse.success(
                aftersaleCommandService.rejectMerchantAftersale(
                        user.username(), aftersaleId, actualRequest));
    }

    @PostMapping("/api/member/merchant/aftersales/{aftersaleId}/receive-return")
    @OperationAudit(module = "Aftersale", businessType = "RECEIVE_RETURN")
    public ApiResponse<AftersaleActionResponse> receiveMerchantReturn(
            @CurrentUser AuthenticatedUser user, @PathVariable Long aftersaleId) {
        permissionGuard.require(user, "merchant:aftersale:approve");
        return ApiResponse.success(
                aftersaleCommandService.receiveMerchantReturn(user.username(), aftersaleId));
    }

    @PostMapping("/api/member/merchant/aftersales/{aftersaleId}/refund")
    @OperationAudit(module = "Aftersale", businessType = "REFUND_AFTERSALE")
    public ApiResponse<AftersaleActionResponse> refundMerchantAftersale(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long aftersaleId,
            @Valid @RequestBody RefundAftersaleRequest request) {
        permissionGuard.require(user, "merchant:aftersale:refund");
        return ApiResponse.success(
                aftersaleCommandService.refundMerchantAftersale(
                        user.username(), aftersaleId, request));
    }
}
