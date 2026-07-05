package com.telecom.scm.order.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.order.dto.request.AdjustMerchantOrderRequest;
import com.telecom.scm.order.dto.request.MerchantApproveOrderRequest;
import com.telecom.scm.order.dto.request.MerchantShipOrderRequest;
import com.telecom.scm.order.dto.response.MerchantOrderDetailResponse;
import com.telecom.scm.order.dto.response.OrderActionResponse;
import com.telecom.scm.order.dto.response.OrderContractResponse;
import com.telecom.scm.order.dto.response.OrderSummaryResponse;
import com.telecom.scm.order.service.OrderCommandService;
import com.telecom.scm.order.service.OrderDocumentService;
import com.telecom.scm.order.service.OrderQueryService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/member/merchant/orders")
public class MerchantOrderController {

    private final OrderQueryService orderService;
    private final OrderCommandService orderCommandService;
    private final OrderDocumentService orderDocumentService;
    private final PermissionGuard permissionGuard;

    public MerchantOrderController(
            OrderQueryService orderService,
            OrderCommandService orderCommandService,
            OrderDocumentService orderDocumentService,
            PermissionGuard permissionGuard) {
        this.orderService = orderService;
        this.orderCommandService = orderCommandService;
        this.orderDocumentService = orderDocumentService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping
    public ApiResponse<PageResult<OrderSummaryResponse>> merchantOrders(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "merchant:order:view");
        return ApiResponse.success(
                orderService.currentMerchantOrders(user.username(), page, pageSize));
    }

    @GetMapping("/{orderId}/detail")
    public ApiResponse<MerchantOrderDetailResponse> merchantOrderDetail(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        permissionGuard.require(user, "merchant:order:view");
        return ApiResponse.success(
                orderService.currentMerchantOrderDetail(user.username(), orderId));
    }

    @GetMapping("/{orderId}/quote-export")
    @OperationAudit(module = "Order", businessType = "EXPORT_MERCHANT_QUOTE")
    public ResponseEntity<byte[]> exportMerchantQuote(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        permissionGuard.require(user, "merchant:order:view");
        return orderDocumentService.exportMerchantQuote(user.username(), orderId);
    }

    @GetMapping("/{orderId}/contracts")
    public ApiResponse<List<OrderContractResponse>> merchantOrderContracts(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        permissionGuard.require(user, "merchant:order:view");
        return ApiResponse.success(
                orderDocumentService.merchantOrderContracts(user.username(), orderId));
    }

    @GetMapping("/{orderId}/contracts/{fileId}/download")
    @OperationAudit(module = "Order", businessType = "DOWNLOAD_ORDER_CONTRACT")
    public ResponseEntity<byte[]> downloadMerchantOrderContract(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long orderId,
            @PathVariable Long fileId) {
        permissionGuard.require(user, "merchant:order:view");
        return orderDocumentService.downloadMerchantOrderContract(user.username(), orderId, fileId);
    }

    @PostMapping("/{orderId}/approve")
    @OperationAudit(module = "Order", businessType = "APPROVE_ORDER")
    public ApiResponse<OrderActionResponse> approveMerchantOrder(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long orderId,
            @RequestBody(required = false) MerchantApproveOrderRequest request) {
        permissionGuard.require(user, "merchant:order:approve");
        MerchantApproveOrderRequest actualRequest =
                request == null ? new MerchantApproveOrderRequest(null) : request;
        return ApiResponse.success(
                orderCommandService.approveMerchantOrder(user.username(), orderId, actualRequest));
    }

    @PostMapping("/{orderId}/adjust")
    @OperationAudit(module = "Order", businessType = "ADJUST_ORDER")
    public ApiResponse<OrderActionResponse> adjustMerchantOrder(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long orderId,
            @Valid @RequestBody AdjustMerchantOrderRequest request) {
        permissionGuard.require(user, "merchant:order:approve");
        return ApiResponse.success(
                orderCommandService.adjustMerchantOrder(user.username(), orderId, request));
    }

    @PostMapping("/{orderId}/ship")
    @OperationAudit(module = "Order", businessType = "SHIP_ORDER")
    public ApiResponse<OrderActionResponse> shipMerchantOrder(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long orderId,
            @Valid @RequestBody MerchantShipOrderRequest request) {
        permissionGuard.require(user, "merchant:order:ship");
        return ApiResponse.success(
                orderCommandService.shipMerchantOrder(user.username(), orderId, request));
    }
}
