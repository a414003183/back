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
import com.telecom.scm.order.dto.request.BindOrderContractRequest;
import com.telecom.scm.order.dto.request.CreateOrderRequest;
import com.telecom.scm.order.dto.request.RegisterOrderPaymentRequest;
import com.telecom.scm.order.dto.response.OrderActionResponse;
import com.telecom.scm.order.dto.response.OrderContractResponse;
import com.telecom.scm.order.dto.response.OrderCreateResponse;
import com.telecom.scm.order.dto.response.OrderSummaryResponse;
import com.telecom.scm.order.service.OrderCommandService;
import com.telecom.scm.order.service.OrderDocumentService;
import com.telecom.scm.order.service.OrderQueryService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/member/customer/orders")
public class CustomerOrderController {

    private final OrderQueryService orderService;
    private final OrderCommandService orderCommandService;
    private final OrderDocumentService orderDocumentService;
    private final PermissionGuard permissionGuard;

    public CustomerOrderController(
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
    public ApiResponse<PageResult<OrderSummaryResponse>> customerOrders(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "customer:order:view");
        return ApiResponse.success(
                orderService.currentCustomerOrders(user.username(), page, pageSize));
    }

    @GetMapping("/{orderId}/quote-export")
    @OperationAudit(module = "Order", businessType = "EXPORT_CUSTOMER_QUOTE")
    public ResponseEntity<byte[]> exportCustomerQuote(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        permissionGuard.require(user, "customer:order:view");
        return orderDocumentService.exportCustomerQuote(user.username(), orderId);
    }

    @GetMapping("/{orderId}/contracts")
    public ApiResponse<List<OrderContractResponse>> customerOrderContracts(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        permissionGuard.require(user, "customer:order:view");
        return ApiResponse.success(
                orderDocumentService.customerOrderContracts(user.username(), orderId));
    }

    @PostMapping("/{orderId}/contracts")
    @OperationAudit(module = "Order", businessType = "BIND_ORDER_CONTRACT")
    public ApiResponse<OrderContractResponse> bindCustomerOrderContract(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long orderId,
            @Valid @RequestBody BindOrderContractRequest request) {
        permissionGuard.require(user, "customer:order:view");
        return ApiResponse.success(
                orderDocumentService.bindCustomerOrderContract(
                        user.username(), orderId, request.fileId()));
    }

    @GetMapping("/{orderId}/contracts/{fileId}/download")
    @OperationAudit(module = "Order", businessType = "DOWNLOAD_ORDER_CONTRACT")
    public ResponseEntity<byte[]> downloadCustomerOrderContract(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long orderId,
            @PathVariable Long fileId) {
        permissionGuard.require(user, "customer:order:view");
        return orderDocumentService.downloadCustomerOrderContract(user.username(), orderId, fileId);
    }

    @PostMapping
    @OperationAudit(module = "Order", businessType = "CREATE_ORDER")
    public ApiResponse<OrderCreateResponse> createCustomerOrder(
            @CurrentUser AuthenticatedUser user, @Valid @RequestBody CreateOrderRequest request) {
        permissionGuard.require(user, "customer:order:view");
        return ApiResponse.success(
                orderCommandService.createCustomerOrder(user.username(), request));
    }

    @PostMapping("/{orderId}/payment-register")
    @OperationAudit(module = "Order", businessType = "PAYMENT_REGISTER")
    public ApiResponse<OrderActionResponse> registerCustomerPayment(
            @CurrentUser AuthenticatedUser user,
            @PathVariable String orderId,
            @Valid @RequestBody RegisterOrderPaymentRequest request) {
        permissionGuard.require(user, "customer:order:view");
        Long actualOrderId = orderService.resolveOrderId(orderId);
        return ApiResponse.success(
                orderCommandService.registerCustomerPayment(
                        user.username(), actualOrderId, request));
    }

    @PostMapping("/{orderId}/confirm-receive")
    @OperationAudit(module = "Order", businessType = "CONFIRM_RECEIVE")
    public ApiResponse<OrderActionResponse> confirmCustomerReceive(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        permissionGuard.require(user, "customer:order:view");
        return ApiResponse.success(
                orderCommandService.confirmCustomerReceive(user.username(), orderId));
    }
}
